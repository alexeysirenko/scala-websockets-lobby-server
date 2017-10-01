package message

import play.api.libs.json._

import scala.util.Try

trait LobbyMessage
trait SecuredLobbyMessage extends LobbyMessage

object LobbyMessage {
  val TYPE_FIELD = "$type"

  def getTypeField(json: JsValue): String = (json \ "$type").as[String]

  implicit class TypedLobbyMessage(message: LobbyMessage) {
    def toJson: JsValue = LobbyMessage.formatWithTypeField(message)
  }

  private val parseFunctions = List(
    AuthorizationMessages.parseJson,
    HeartbeatMessages.parseJson,
    SubscriptionMessages.parseJson,
    TableManagementMessages.parseJson
  )

  private val formatFunctions = List(
    AuthorizationMessages.toJson,
    HeartbeatMessages.toJson,
    SubscriptionMessages.toJson,
    TableManagementMessages.toJson
  )

  def parseTypeField(json: JsValue): Try[LobbyMessage] = Try {
    parseFunctions.flatMap(f => f.lift(json)).headOption match {
      case Some(lobbyMessage) => lobbyMessage
      case None => throw new Error(s"Unknown message type '${json.as[String]}'")
    }
  }

  def formatWithTypeField(message: LobbyMessage): JsValue = {
    formatFunctions.flatMap(f => f.lift(message)).headOption match {
      case Some(json) => json
      case None => throw new Error(s"Unknown message type '$message'")
    }
  }
}

