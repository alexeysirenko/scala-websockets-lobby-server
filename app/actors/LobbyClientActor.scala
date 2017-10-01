package actors

import akka.actor.Status
import akka.actor.{Actor, ActorRef, Props, Status}
import akka.event.Logging
import msg._
import play.api.libs.json.JsValue

import scala.util.{Failure, Success}

class LobbyClientActor(out: ActorRef, authActor: ActorRef) extends Actor {

  val log = Logging(context.system, this)

  override def receive = {
    case msg: JsValue => receiveRawJson(msg)
    case msg: LobbyMessage => receiveLobbyMessage(msg)
    case _ => unhandled()
  }

  def receiveRawJson(msg: JsValue): Unit = {
    LobbyMessage.parseTypeField(msg) match {
      case Success(lobbyMessage) =>
        lobbyMessage match {
          case login: Login => authActor ! login
          case Ping(seq) => out ! Pong(seq)
          case _ => unhandled()
        }
      case Failure(e) => out ! Status.Failure(e)
    }
  }

  def receiveLobbyMessage(msg: LobbyMessage): Unit = {
    msg match {
      case loginSuccessful: LoginSuccessful => out ! loginSuccessful.toJson
      case loginFailed: LoginFailed => out ! loginFailed.toJson
      case _ => unhandled()
    }
  }
}

object LobbyClientActor {
  def props(out: ActorRef, authActor: ActorRef) = Props(new LobbyClientActor(out, authActor))
}

