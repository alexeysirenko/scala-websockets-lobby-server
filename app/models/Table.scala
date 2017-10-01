package models

import play.api.libs.json.{Json, OFormat}

case class Table(id: Option[Int], name: String, participants: Int) {
  def withId(id: Int): Table = this.copy(id = Some(id))
}
object Table {
  implicit val tableFormat: OFormat[Table] = Json.format[Table]
}
