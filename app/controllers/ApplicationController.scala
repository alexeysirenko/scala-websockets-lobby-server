package controllers

import java.net.URL
import javax.inject._

import actors.{AuthorizationActor, LobbyClientActor, TablesActor}
import akka.actor.ActorSystem
import akka.stream.Materializer
import message.LobbyMessage
import play.api._
import play.api.libs.json.JsValue
import play.api.libs.streams.ActorFlow
import play.api.mvc.WebSocket.MessageFlowTransformer
import play.api.mvc._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
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
