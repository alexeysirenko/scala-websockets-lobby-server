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
          case Ping(seq) => out ! Pong(seq).toJson
          case SubscribeTables => tablesActor ! SubscribeTables
          case UnsubscribeTables => tablesActor ! UnsubscribeTables
          case msg: SecuredLobbyMessage => receiveSecuredMessage(msg)
          case _ => unhandled()
        }
      case Failure(e) => out ! Status.Failure(e)
    }
  }

  private def receiveLobbyMessage(msg: LobbyMessage): Unit = {
    msg match {
      case msg: LoginSuccessful =>
        userType = Option(msg.userType)
        out ! msg.toJson
      case msg: LoginFailed => out ! msg.toJson
      case msg: TableList => out ! msg.toJson
      case msg: TableAdded => out ! msg.toJson
      case msg: TableUpdated => out ! msg.toJson
      case msg: TableRemoved => out ! msg.toJson
      case msg: TableUpdateFailed => out ! msg.toJson
      case msg: TableRemoveFailed => out ! msg.toJson
      case _ => unhandled()
    }
  }

  private def receiveSecuredMessage(msg: SecuredLobbyMessage): Unit = {
    if (isAdmin) {
      msg match {
        case _: AddTable => tablesActor ! msg
        case msg @ UpdateTable(table) =>
          out ! TableUpdated(table).toJson
          tablesActor ! msg
        case msg @ RemoveTable(id) =>
          out ! TableRemoved(id).toJson
          tablesActor ! msg
        case _ => unhandled()
      }
    } else {
      out ! NotAuthorized.toJson
    }
  }

  private def isAdmin = userType.contains("admin")

}

object LobbyClientActor {
  def props(out: ActorRef, authActor: ActorRef, tablesActor: ActorRef) =
    Props(new LobbyClientActor(out, authActor, tablesActor))
}

