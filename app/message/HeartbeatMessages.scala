package message

import play.api.libs.json.{JsObject, JsValue, Json, OFormat}

case class Ping(seq: Int) extends LobbyMessage
case class Pong(seq: Int) extends LobbyMessage

object Ping {
  val msgType = "ping"
  implicit val format: OFormat[Ping] = Json.format[Ping]
}

object Pong {
  val msgType = "pong"
  implicit val format: OFormat[Pong] = Json.format[Pong]
}

object HeartbeatMessages {

  import LobbyMessage._

  def parseJson: PartialFunction[JsValue, LobbyMessage] = {
    case json: JsValue if getTypeField(json) == Ping.msgType => json.as[Ping]
    case json: JsValue if getTypeField(json) == Pong.msgType => json.as[Pong]
  }

  def toJson: PartialFunction[LobbyMessage, JsValue] = {
    case msg: Ping => Json.obj(TYPE_FIELD -> Ping.msgType) ++ Json.toJson(msg).as[JsObject]
    case msg: Pong => Json.obj(TYPE_FIELD -> Pong.msgType) ++ Json.toJson(msg).as[JsObject]
  }

}
