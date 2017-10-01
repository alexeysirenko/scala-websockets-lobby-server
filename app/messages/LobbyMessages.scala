package messages

import play.api.libs.json._

import scala.util.Try

trait LobbyMessage
trait SecuredLobbyMessage extends LobbyMessage

object LobbyMessage {
  private val TYPE_FIELD = "$type"

  implicit class TypedLobbyMessage(message: LobbyMessage) {
    def toJson: JsValue = LobbyMessage.formatWithTypeField(message)
  }

  def parseTypeField(json: JsValue): Try[LobbyMessage] = Try {
    (json \ TYPE_FIELD).as[String] match {
      case Login.msgType => json.as[Login]
      case LoginFailed.msgType => json.as[LoginFailed]
      case LoginSuccessful.msgType => json.as[LoginSuccessful]
      case Ping.msgType => json.as[Ping]
      case Pong.msgType => json.as[Pong]
      case SubscribeTables.msgType => SubscribeTables
      case UnsubscribeTables.msgType => UnsubscribeTables
      case TableList.msgType => json.as[TableList]
      case NotAuthorized.msgType => json.as[TableList]
      case AddTable.msgType => json.as[AddTable]
      case RemoveTable.msgType => json.as[RemoveTable]
      case UpdateTable.msgType => json.as[UpdateTable]
      case TableAdded.msgType => json.as[AddTable]
      case TableUpdated.msgType => json.as[UpdateTable]
      case TableRemoved.msgType => json.as[RemoveTable]
      case TableUpdateFailed.msgType => json.as[TableUpdateFailed]
      case TableRemoveFailed.msgType => json.as[TableRemoveFailed]
      case any => throw new Error(s"Unknown message type '$any'")
    }
  }

  def formatWithTypeField(message: LobbyMessage): JsValue = message match {
    case msg: Login => Json.obj(TYPE_FIELD -> Login.msgType) ++ Json.toJson(msg).as[JsObject]
    case msg: LoginFailed => Json.obj(TYPE_FIELD -> LoginFailed.msgType) ++ Json.toJson(msg).as[JsObject]
    case msg: LoginSuccessful => Json.obj(TYPE_FIELD -> LoginSuccessful.msgType) ++ Json.toJson(msg).as[JsObject]
    case msg: Ping => Json.obj(TYPE_FIELD -> Ping.msgType) ++ Json.toJson(msg).as[JsObject]
    case msg: Pong => Json.obj(TYPE_FIELD -> Pong.msgType) ++ Json.toJson(msg).as[JsObject]
    case SubscribeTables => Json.obj(TYPE_FIELD -> SubscribeTables.msgType)
    case UnsubscribeTables => Json.obj(TYPE_FIELD -> UnsubscribeTables.msgType)
    case msg: TableList => Json.obj(TYPE_FIELD -> TableList.msgType) ++ Json.toJson(msg).as[JsObject]
    case NotAuthorized => Json.obj(TYPE_FIELD -> NotAuthorized.msgType)
    case msg: AddTable => Json.obj(TYPE_FIELD -> AddTable.msgType) ++ Json.toJson(msg).as[JsObject]
    case msg: UpdateTable => Json.obj(TYPE_FIELD -> UpdateTable.msgType) ++ Json.toJson(msg).as[JsObject]
    case msg: RemoveTable => Json.obj(TYPE_FIELD -> RemoveTable.msgType) ++ Json.toJson(msg).as[JsObject]
    case msg: TableAdded => Json.obj(TYPE_FIELD -> TableAdded.msgType) ++ Json.toJson(msg).as[JsObject]
    case msg: TableUpdated => Json.obj(TYPE_FIELD -> TableUpdated.msgType) ++ Json.toJson(msg).as[JsObject]
    case msg: TableRemoved => Json.obj(TYPE_FIELD -> TableRemoved.msgType) ++ Json.toJson(msg).as[JsObject]
    case msg: TableUpdateFailed => Json.obj(TYPE_FIELD -> TableUpdateFailed.msgType) ++ Json.toJson(msg).as[JsObject]
    case msg: TableRemoveFailed => Json.obj(TYPE_FIELD -> TableRemoveFailed.msgType) ++ Json.toJson(msg).as[JsObject]
  }
}

