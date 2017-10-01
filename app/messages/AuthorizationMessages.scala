package messages

import play.api.libs.json.{Format, Json, OFormat, __}
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