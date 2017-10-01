package messages

import play.api.libs.json.{Json, OFormat}

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
