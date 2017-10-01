package msg

import models.Table
import play.api.libs.json._
import play.api.libs.functional.syntax._

import scala.util.Try

sealed trait LobbyMessage
sealed trait SecuredLobbyMessage extends LobbyMessage
case class Login(username: String, password: String) extends LobbyMessage
case class LoginFailed(reason: String) extends LobbyMessage
case class LoginSuccessful(userType: String) extends LobbyMessage
case class Ping(seq: Int) extends LobbyMessage
case class Pong(seq: Int) extends LobbyMessage
case object SubscribeTables extends LobbyMessage {
  val msgType = "subscribe_tables"
}

case class TableList(table: List[Table]) extends LobbyMessage
case object UnsubscribeTables extends LobbyMessage {
  val msgType = "unsubscribe_tables"
}
case object NotAuthorized extends LobbyMessage {
  val msgType = "not_authorized"
}
case class AddTable(table: Table) extends SecuredLobbyMessage
case class UpdateTable(table: Table) extends SecuredLobbyMessage
case class RemoveTable(id: Int) extends SecuredLobbyMessage
case class TableAdded(table: Table) extends LobbyMessage
case class TableUpdated(table: Table) extends LobbyMessage
case class TableRemoved(id: Int) extends LobbyMessage
case class TableUpdateFailed(id: Int) extends LobbyMessage
case class TableRemoveFailed(id: Int) extends LobbyMessage

object Login {
  val msgType = "login"
  implicit val format: OFormat[Login] = Json.format[Login]
}

object LoginFailed {
  val msgType = "login_failed"
  implicit val format: OFormat[LoginFailed] = Json.format[LoginFailed]
}

object LoginSuccessful {
  val msgType = "login_successful"
  implicit val format: OFormat[LoginSuccessful] = Json.format[LoginSuccessful]
  implicit val reads: Reads[LoginSuccessful] = (__ \ "user_type").read[String].map { userType =>
    LoginSuccessful(userType)
  }
  implicit val writes: Writes[LoginSuccessful] = (__ \ "user_type").write[String].contramap {
    (message: LoginSuccessful) => message.userType
  }
}

object Ping {
  val msgType = "ping"
  implicit val format: OFormat[Ping] = Json.format[Ping]
}

object Pong {
  val msgType = "pong"
  implicit val format: OFormat[Pong] = Json.format[Pong]
}

object TableList {
  val msgType = "table_list"
  implicit val format: OFormat[TableList] = Json.format[TableList]

}

object AddTable {
  val msgType = "add_table"
  implicit val format: OFormat[AddTable] = Json.format[AddTable]
}

object UpdateTable {
  val msgType = "update_table"
  implicit val format: OFormat[UpdateTable] = Json.format[UpdateTable]
}

object RemoveTable {
  val msgType = "remove_table"
  implicit val format: OFormat[RemoveTable] = Json.format[RemoveTable]
}

object TableAdded {
  val msgType = "table_added"
  implicit val format: OFormat[TableAdded] = Json.format[TableAdded]
}

object TableUpdated {
  val msgType = "table_updated"
  implicit val format: OFormat[TableUpdated] = Json.format[TableUpdated]
}

object TableRemoved {
  val msgType = "table_removed"
  implicit val format: OFormat[TableRemoved] = Json.format[TableRemoved]
}

object TableUpdateFailed {
  val msgType = "update_failed"
  implicit val format: OFormat[TableUpdateFailed] = Json.format[TableUpdateFailed]
}

object TableRemoveFailed {
  val msgType = "removal_failed"
  implicit val format: OFormat[TableRemoveFailed] = Json.format[TableRemoveFailed]
}

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

