package controllers

import javax.inject._

import actors.{AuthorizationActor, LobbyClientActor, TablesActor}
import akka.actor.ActorSystem
import akka.stream.Materializer
import play.api.libs.json.JsValue
import play.api.libs.streams.ActorFlow
import play.api.mvc._

@Singleton
class ApplicationController @Inject()(cc: ControllerComponents)(implicit system: ActorSystem, mat: Materializer)
  extends AbstractController(cc) {

  private val authorizationActor = system.actorOf(AuthorizationActor.props(), "authorization-actor")
  private val tablesActor = system.actorOf(TablesActor.props(), "tables-actor")

  def lobby: WebSocket = WebSocket.accept[JsValue, JsValue] { request =>
    ActorFlow.actorRef { out =>
      LobbyClientActor.props(out, authorizationActor, tablesActor)
    }
  }

  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }
}
