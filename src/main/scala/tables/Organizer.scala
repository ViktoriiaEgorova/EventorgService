package tables

import api.OrganizerNotFoundException
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import slick.dbio.{DBIO, DBIOAction, Effect, NoStream}
import slick.jdbc.H2Profile.api._
import slick.lifted.{ForeignKeyQuery, ProvenShape, TableQuery}
import scala.concurrent.ExecutionContext.Implicits.global

case class Organizer(id_org: Option[Long],
                     name: String,
                     events: Option[Int] = None
                    )

object Organizer {
  implicit val jsonDecoder: Decoder[Organizer] = deriveDecoder
  implicit val jsonEncoder: Encoder[Organizer] = deriveEncoder
}

class OrganizerTable(tag: Tag) extends Table[Organizer](tag, "ORGANIZERS") {

  def orgId: Rep[Option[Long]] = column("ORG_ID", O.PrimaryKey, O.AutoInc)

  def name: Rep[String] = column("ORGANIZER_NAME", O.Unique)

  def events: Rep[Option[Int]] = column("ORGANIZER_EVENTS")

  //  def organizer: ForeignKeyQuery[OrganizerTable, Organizer] =
  //    foreignKey("ORGINIZER_FK", orgId, OrganizerQueryRepository.AllCustomers)(_.id)

  //override def * : ProvenShape[Organizer] = (orgId, name, events).mapTo[Organizer]
  override def * : ProvenShape[Organizer] = (orgId, name, events) <> ((Organizer.apply _).tupled, Organizer.unapply)

}

object OrganizerQueryRepository {

  val AllOrganizers = TableQuery[OrganizerTable]

//  def findOrganizer(id: Long): DIO[Option[Organizer], Effect.Read] =
//    AllOrganizers
//      .filter(_.orgId === id)
//      .result
//      .headOption


  def findOrganizer(orgId: Long): DIO[Option[Organizer], Effect.Read] = {
    val query = AllOrganizers.filter(_.orgId === orgId)
    query.exists.result.flatMap(existingRecord =>
      if (existingRecord) {
        AllOrganizers
              .filter(_.orgId === orgId)
              .result
              .headOption
      }
      else {
        throw OrganizerNotFoundException(orgId)
      }
    )

  }


  def findOrganizerByName(name: String): DIO[Option[Organizer], Effect.Read] =
    AllOrganizers
      .filter(_.name === name)
      .result
      .headOption


  def addOrganizer(organizer: Organizer): DIO[Int, Effect.Write] =
    AllOrganizers += organizer

  def updateNumberEvents(orgId: Long, new_events: Int): DIO[Int, Effect.Write] = {
    AllOrganizers
      .filter(_.orgId === orgId)
      .map(_.events)
      .update(Some(new_events))

  }


}