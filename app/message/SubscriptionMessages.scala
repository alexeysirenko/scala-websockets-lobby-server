package message

import models.Table
import play.api.libs.json.{JsObject, JsValue, Json, OFormat}

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

object SubscriptionMessages {

  import LobbyMessage._

  def parseJson: PartialFunction[JsValue, LobbyMessage] = {
    case json: JsValue if getTypeField(json) == SubscribeTables.msgType => SubscribeTables
    case json: JsValue if getTypeField(json) == UnsubscribeTables.msgType => UnsubscribeTables
    case json: JsValue if getTypeField(json) == TableList.msgType => json.as[TableList]
  }

  def toJson: PartialFunction[LobbyMessage, JsValue] = {
    case SubscribeTables => Json.obj(TYPE_FIELD -> SubscribeTables.msgType)
    case UnsubscribeTables => Json.obj(TYPE_FIELD -> UnsubscribeTables.msgType)
    case msg: TableList => Json.obj(TYPE_FIELD -> TableList.msgType) ++ Json.toJson(msg).as[JsObject]
  }

}