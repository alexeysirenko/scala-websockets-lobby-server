package actors

import akka.actor.Status
import akka.actor.{Actor, ActorRef, Props, Status}
import akka.event.Logging
import msg.{LobbyMessage, Login, LoginFailed}
import play.api.libs.json.JsValue

import scala.util.{Failure, Success}

class LobbyActor(out: ActorRef) extends Actor {

  val log = Logging(context.system, this)

  override def preStart() = {
    log.info("Starting")
  }

  override def postStop() = {
    log.info("Stopping")
  }

  override def receive = {
    case msg: JsValue =>
      log.info(s"Received a message $msg")
      LobbyMessage.parse(msg) match {
        case Success(lobbyMessage) =>
          log.info(s"Parsed message: $lobbyMessage")
          lobbyMessage match {
            case login: Login => out ! LoginFailed("foo").toTypedResponse
          }

        case Failure(e) => out ! Status.Failure(e)
      }
  }
}

object LobbyActor {
  def props(out: ActorRef) = Props(new LobbyActor(out))
}

