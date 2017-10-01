package models

import play.api.libs.json.{Json, OFormat}

case class Table(id: Int, name: String, participants: Int)
object Table {
  implicit val tableFormat: OFormat[Table] = Json.format[Table]
}
