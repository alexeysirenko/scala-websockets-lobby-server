package message

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class Login(username: String, password: String) extends LobbyMessage
case class LoginFailed(reason: String) extends LobbyMessage
case class LoginSuccessful(userType: String) extends LobbyMessage
case object NotAuthorized extends LobbyMessage {
  val msgType = "not_authorized"
}

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
  implicit val format: Format[LoginSuccessful] =
    (__ \ "user_type").format[String].inmap(name => LoginSuccessful(name), (ls: LoginSuccessful) => ls.userType)
}

object AuthorizationMessages {

  import LobbyMessage._

  def parseJson: PartialFunction[JsValue, LobbyMessage] = {
    case json: JsValue if getTypeField(json) == Login.msgType => json.as[Login]
    case json: JsValue if getTypeField(json) == LoginFailed.msgType => json.as[LoginFailed]
    case json: JsValue if getTypeField(json) == LoginSuccessful.msgType => json.as[LoginSuccessful]
    case json: JsValue if getTypeField(json) == NotAuthorized.msgType => NotAuthorized
  }

  def toJson: PartialFunction[LobbyMessage, JsValue] = {
    case msg: Login => Json.obj(TYPE_FIELD -> Login.msgType) ++ Json.toJson(msg).as[JsObject]
    case msg: LoginFailed => Json.obj(TYPE_FIELD -> LoginFailed.msgType) ++ Json.toJson(msg).as[JsObject]
    case msg: LoginSuccessful => Json.obj(TYPE_FIELD -> LoginSuccessful.msgType) ++ Json.toJson(msg).as[JsObject]
    case NotAuthorized => Json.obj(TYPE_FIELD -> NotAuthorized.msgType)
  }

}