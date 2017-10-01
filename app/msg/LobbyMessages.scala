package msg

import play.api.libs.json._
import play.api.libs.functional.syntax._

import scala.util.Try

sealed trait LobbyMessage
case class Login(username: String, password: String) extends LobbyMessage
case class LoginFailed(reason: String) extends LobbyMessage
case class LoginSuccessful(userType: String) extends LobbyMessage
case class Ping(seq: Int) extends LobbyMessage
case class Pong(seq: Int) extends LobbyMessage

object Login {

  val msgType = "login"

  implicit val format: OFormat[Login] = Json.format[Login]

  /*implicit val reads: Reads[Login] = (
    (JsPath \ "username").read[String] and
    (JsPath \ "password").read[String]
  )(Login.apply _)

  implicit val writes: Writes[Login] = (
    (JsPath \ "username").write[String] and
    (JsPath \ "password").write[String]
  )(unlift(Login.unapply))*/

}

object LoginFailed {

  val msgType = "login_failed"

  implicit val format: OFormat[LoginFailed] = Json.format[LoginFailed]

  /*implicit val reads: Reads[LoginFailed] = (__ \ "reason").read[String].map { reason => LoginFailed(reason) }

  implicit val writes: Writes[LoginFailed] = (__ \ "reason").write[String].contramap {
    (loginFailed: LoginFailed) => loginFailed.reason
  }*/
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

object LobbyMessage {

  implicit class TypedLobbyMessage(message: LobbyMessage) {
    def toJson: JsValue = LobbyMessage.formatWithTypeField(message)
  }

  private val TYPE_FIELD = "$type"

  def parseTypeField(json: JsValue): Try[LobbyMessage] = Try {
    (json \ TYPE_FIELD).as[String] match {
      case Login.msgType => json.as[Login]
      case LoginFailed.msgType => json.as[LoginFailed]
      case LoginSuccessful.msgType => json.as[LoginSuccessful]
      case Ping.msgType => json.as[Ping]
      case Pong.msgType => json.as[Pong]
      case any => throw new Error(s"Unknown message type '$any'")
    }
  }

  def formatWithTypeField(message: LobbyMessage): JsValue = message match {
    case msg: Login => Json.obj(TYPE_FIELD -> Login.msgType) ++ Json.toJson(msg).as[JsObject]
    case msg: LoginFailed => Json.obj(TYPE_FIELD -> LoginFailed.msgType) ++ Json.toJson(msg).as[JsObject]
    case msg: LoginSuccessful => Json.obj(TYPE_FIELD -> LoginSuccessful.msgType) ++ Json.toJson(msg).as[JsObject]
    case msg: Ping => Json.obj(TYPE_FIELD -> Ping.msgType) ++ Json.toJson(msg).as[JsObject]
    case msg: Pong => Json.obj(TYPE_FIELD -> Pong.msgType) ++ Json.toJson(msg).as[JsObject]
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

