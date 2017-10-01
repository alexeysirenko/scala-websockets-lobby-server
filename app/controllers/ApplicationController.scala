package controllers

import java.net.URL
import javax.inject._

import actors.{AuthorizationActor, LobbyClientActor, TablesActor}
import akka.actor.ActorSystem
import akka.stream.Materializer
import messages.LobbyMessage
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

  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  def lobby: WebSocket = WebSocket.accept[JsValue, JsValue] { request =>
    ActorFlow.actorRef { out =>
      LobbyClientActor.props(out, authorizationActor, tablesActor)
    }
  }

  /**
    * Checks that the WebSocket comes from the same origin.  This is necessary to protect
    * against Cross-Site WebSocket Hijacking as WebSocket does not implement Same Origin Policy.
    *
    * See https://tools.ietf.org/html/rfc6455#section-1.3 and
    * http://blog.dewhurstsecurity.com/2013/08/30/security-testing-html5-websockets.html
    */
  private def sameOriginCheck(implicit rh: RequestHeader): Boolean = {
    // The Origin header is the domain the request originates from.
    // https://tools.ietf.org/html/rfc6454#section-7
    Logger.debug("Checking the ORIGIN ")

    rh.headers.get("Origin") match {
      case Some(originValue) if originMatches(originValue) =>
        Logger.debug(s"originCheck: originValue = $originValue")
        true

      case Some(badOrigin) =>
        Logger.error(s"originCheck: rejecting request because Origin header value ${badOrigin} is not in the same origin")
        false

      case None =>
        Logger.error("originCheck: rejecting request because no Origin header found")
        false
    }
  }

  /**
    * Returns true if the value of the Origin header contains an acceptable value.
    */
  private def originMatches(origin: String): Boolean = {
    try {
      val url = new URL(origin)
      url.getHost == "localhost" &&
        (url.getPort match { case 9000 | 19001 => true; case _ => false })
    } catch {
      case e: Exception => false
    }
  }

}
