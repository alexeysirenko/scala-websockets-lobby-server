package msg

import play.api.libs.json._
import play.api.libs.functional.syntax._

import scala.util.Try

sealed trait LobbyMessage
case class Login(username: String, password: String) extends LobbyMessage
case class LoginFailed(reason: String) extends LobbyMessage
//case class LoggingSucceed(userType: String) extends LobbyMessage
//case class Ping(seq: Int) extends LobbyMessage
//case class Pong(seq: Int) extends LobbyMessage

object Login {

  val msgType = "login"

  implicit val format: OFormat[Login] = Json.format[Login]

  implicit val reads: Reads[Login] = (
    (JsPath \ "username").read[String] and
    (JsPath \ "password").read[String]
  )(Login.apply _)

  implicit val writes: Writes[Login] = (
    (JsPath \ "username").write[String] and
    (JsPath \ "password").write[String]
  )(unlift(Login.unapply))

}

object LoginFailed {

  val msgType = "login_failed"

  implicit val format: OFormat[LoginFailed] = Json.format[LoginFailed]

  implicit val reads: Reads[LoginFailed] = (__ \ "reason").read[String].map { reason => LoginFailed(reason) }

  implicit val writes: Writes[LoginFailed] = (__ \ "reason").write[String].contramap {
    (loginFailed: LoginFailed) => loginFailed.reason
  }
}



object LobbyMessage {

  implicit class TypedLobbyMessage(message: LobbyMessage) {
    def toTypedResponse: JsValue = LobbyMessage.format(message)
  }

  def parse(json: JsValue): Try[LobbyMessage] = Try {
    (json \ "$type").as[String] match {
      case Login.msgType => json.as[Login]
      case LoginFailed.msgType => json.as[LoginFailed]
    }
  }

  def format(message: LobbyMessage): JsValue = message match {
    case message: Login => Json.obj("$type" -> Login.msgType) ++ Json.toJson(message).as[JsObject]
    case message: LoginFailed => Json.obj("$type" -> LoginFailed.msgType) ++ Json.toJson(message).as[JsObject]
  }

  /*implicit val format: OFormat[LobbyMessage] = Json.format[LobbyMessage]

  implicit val reads: Reads[LobbyMessage] = new Reads[LobbyMessage] {
    def reads(json: JsValue): LobbyMessage = {
      (json \ "$type").as[String] match {
        case Login.msgType => json.validate[Login](json)
      }
    }
  }

  implicit val writes: Writes[LobbyMessage] = {
    case message: Login => Json.obj("$type" -> Login.msgType) ++ Json.toJson(message).as[JsObject]
    case message: LoginFailed => Json.obj("$type" -> LoginFailed.msgType) ++ Json.toJson(message).as[JsObject]
  }*/
}

