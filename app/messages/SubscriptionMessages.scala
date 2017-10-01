package messages

import models.Table
import play.api.libs.json.{Json, OFormat}

case class TableList(table: List[Table]) extends LobbyMessage

case object SubscribeTables extends LobbyMessage {
  val msgType = "subscribe_tables"
}

case object UnsubscribeTables extends LobbyMessage {
  val msgType = "unsubscribe_tables"
}

object TableList {
  val msgType = "table_list"
  implicit val format: OFormat[TableList] = Json.format[TableList]
}