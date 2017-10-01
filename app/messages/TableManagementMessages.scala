package messages

import models.Table
import play.api.libs.json.{Json, OFormat}

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