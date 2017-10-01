package actors

import akka.actor.{Actor, ActorRef, Props, Status}
import akka.event.Logging
import msg._
import play.api.libs.json.JsValue

import scala.util.{Failure, Success}

class LobbyClientActor(out: ActorRef, authActor: ActorRef, tablesActor: ActorRef) extends Actor {

  val log = Logging(context.system, this)

  var userType: Option[String] = None

  override def postStop(): Unit = {
    tablesActor ! UnsubscribeTables
  }

  override def receive = {
    case msg: JsValue => receiveRawJson(msg)
    case msg: LobbyMessage => receiveLobbyMessage(msg)
    case _ => unhandled()
  }

  private def receiveRawJson(msg: JsValue): Unit = {
    LobbyMessage.parseTypeField(msg) match {
      case Success(lobbyMessage) =>
        lobbyMessage match {
          case login: Login => authActor ! login
          case Ping(seq) => respondToClient(Pong(seq))
          case SubscribeTables => tablesActor ! SubscribeTables
          case UnsubscribeTables => tablesActor ! UnsubscribeTables
          case msg: SecuredLobbyMessage => receiveSecuredLobbyMessage(msg)
          case _ => unhandled()
        }
      case Failure(e) => out ! Status.Failure(e)
    }
  }

  private def receiveLobbyMessage(message: LobbyMessage): Unit = {
    message match {
      case msg: LoginSuccessful =>
        userType = Option(msg.userType)
        respondToClient(msg)
      case msg => respondToClient(msg)
    }
  }

  private def receiveSecuredLobbyMessage(msg: SecuredLobbyMessage): Unit = {
    if (isAdmin) {
      msg match {
        case _: AddTable => tablesActor ! msg
        case msg @ UpdateTable(table) =>
          respondToClient(TableUpdated(table))
          tablesActor ! msg
        case msg @ RemoveTable(id) =>
          respondToClient(TableRemoved(id))
          tablesActor ! msg
        case _ => unhandled()
      }
    } else {
      respondToClient(NotAuthorized)
    }
  }

  private def respondToClient(message: LobbyMessage): Unit = out ! message.toJson

  private def isAdmin = userType.contains("admin")

}

object LobbyClientActor {
  def props(out: ActorRef, authActor: ActorRef, tablesActor: ActorRef) =
    Props(new LobbyClientActor(out, authActor, tablesActor))
}

