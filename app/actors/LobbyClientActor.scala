package actors

import akka.actor.{Actor, ActorRef, Props, Status}
import message._
import play.api.libs.json.JsValue

import scala.util.{Failure, Success}

class LobbyClientActor(out: ActorRef, authActor: ActorRef, tablesActor: ActorRef) extends Actor {

  /**
    * Role  of the current user (if authorized)
    */
  var userType: Option[String] = None

  override def postStop(): Unit = {
    tablesActor ! UnsubscribeTables
  }

  override def receive = {
    case msg: JsValue => receiveRawJson(msg)
    case msg: LobbyMessage => receiveLobbyMessage(msg)
    case msg => unhandled(msg)
  }

  /**
    * Handles raw JSON input from the client.
    * @param msg json
    */
  private def receiveRawJson(msg: JsValue): Unit = {
    LobbyMessage.parseTypeField(msg) match {
      case Success(lobbyMessage) =>
        lobbyMessage match {
          case login: Login => authActor ! login
          case Ping(seq) => respondToClient(Pong(seq))
          case SubscribeTables => tablesActor ! SubscribeTables
          case UnsubscribeTables => tablesActor ! UnsubscribeTables
          case msg: SecuredLobbyMessage => receiveSecuredLobbyMessage(msg)
          case msg => unhandled(msg)
        }
      case Failure(e) => out ! Status.Failure(e)
    }
  }

  /**
    * Handles responses from another actors.
    * @param message LobbyMessage instance
    */
  private def receiveLobbyMessage(message: LobbyMessage): Unit = {
    message match {
      case msg: LoginSuccessful =>
        userType = Option(msg.userType)
        respondToClient(msg)
      case msg => respondToClient(msg)
    }
  }

  /**
    * Handles secured requests from the client.
    * @param msg
    */
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
        case msg => unhandled(msg)
      }
    } else {
      respondToClient(NotAuthorized)
    }
  }

  /**
    * Responds to the remote client, serializes message to JSON.
    * @param message message to respond with
    */
  private def respondToClient(message: LobbyMessage): Unit = out ! message.toJson

  /**
    * Checks privileges of the current user.
    * @return true if user has administration rights, returns false otherwise
    */
  private def isAdmin = userType.contains("admin")

}

object LobbyClientActor {

  /**
    * Creates LobbyClientActor's props instance.
    *
    * @param out actor to answer to
    * @param authActor authorization actor
    * @param tablesActor table management actor
    * @return props instance
    */
  def props(out: ActorRef, authActor: ActorRef, tablesActor: ActorRef) =
    Props(new LobbyClientActor(out, authActor, tablesActor))
}

