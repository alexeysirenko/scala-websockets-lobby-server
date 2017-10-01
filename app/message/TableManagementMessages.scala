package message

import models.Table
import play.api.libs.json.{JsObject, JsValue, Json, OFormat}

case class AddTable(table: Table) extends SecuredLobbyMessage
case class UpdateTable(table: Table) extends SecuredLobbyMessage
case class RemoveTable(id: Int) extends SecuredLobbyMessage
case class TableAdded(table: Table) extends LobbyMessage
case class TableUpdated(table: Table) extends LobbyMessage
case class TableRemoved(id: Int) extends LobbyMessage
case class TableUpdateFailed(id: Int) extends LobbyMessage
case class TableRemoveFailed(id: Int) extends LobbyMessage

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

object TableManagementMessages {

  import LobbyMessage._

  def parseJson: PartialFunction[JsValue, LobbyMessage] = {
    case json: JsValue if getTypeField(json) == AddTable.msgType => json.as[AddTable]
    case json: JsValue if getTypeField(json) == RemoveTable.msgType => json.as[RemoveTable]
    case json: JsValue if getTypeField(json) == UpdateTable.msgType => json.as[UpdateTable]
    case json: JsValue if getTypeField(json) == TableAdded.msgType => json.as[AddTable]
    case json: JsValue if getTypeField(json) == TableUpdated.msgType => json.as[UpdateTable]
    case json: JsValue if getTypeField(json) == TableRemoved.msgType => json.as[RemoveTable]
    case json: JsValue if getTypeField(json) == TableUpdateFailed.msgType => json.as[TableUpdateFailed]
    case json: JsValue if getTypeField(json) == TableRemoveFailed.msgType => json.as[TableRemoveFailed]
  }

  def toJson: PartialFunction[LobbyMessage, JsValue] = {
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