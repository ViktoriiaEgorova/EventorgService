package tables

import api.{EventNotFoundIdException, NoSuchDateException, NoSuchRegionException, NoSuchTopicException}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import slick.jdbc.H2Profile.api._
import slick.lifted.{ForeignKeyQuery, ProvenShape}
import tables.EventUserQueryRepository.AllEventUsers

import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global

case class Event(id_event: Option[Long] = None,
                 id_org: Long,
                 name: String,
                 topic: String,
                 date: String,
                 time: String,
                 price: Int,
                 description: String,
                 region: String,
                 visitors: Int = 0,
                 reviews: Option[String] = None
                )


object Event {
  implicit val jsonDecoder: Decoder[Event] = deriveDecoder
  implicit val jsonEncoder: Encoder[Event] = deriveEncoder
}


class EventTable(tag: Tag) extends Table[Event](tag, "EVENTS") {

  def eventId: Rep[Option[Long]] = column("EVENT_ID", O.PrimaryKey, O.AutoInc)

  def orgId: Rep[Long] = column("ORG_ID")

  def name: Rep[String] = column("EVENT_NAME", O.Unique)

  def topic: Rep[String] = column("EVENT_TOPIC")

  def date: Rep[String] = column("DATE")

  def time: Rep[String] = column("TIME")

  def price: Rep[Int] = column("PRICE")

  def description: Rep[String] = column("DESCRIPTION")

  def region: Rep[String] = column("REGION")

  def visitors: Rep[Int] = column("VISITORS")

  def reviews: Rep[Option[String]] = column("REVIEWS")
//
//  def organizer: ForeignKeyQuery[OrganizerTable, Organizer] =
//    foreignKey("ORGANIZER_FK", orgId, OrganizerQueryRepository.AllOrganizers)(_.orgId)

  //  def user: ForeignKeyQuery[UserTable, User] =
  //    foreignKey("USER_FK", visitors, UserQueryRepository.AllUsers)(_.userId)

//  def eventuser: ForeignKeyQuery[EventUserTable, EventUser] =
//    foreignKey("EVENTUSER_FK", eventId, EventUserQueryRepository.AllEventUsers)(_.eventId)


  //override def * : ProvenShape[Event] = (eventId, orgId, name, topic, date_time, price, description, region, visitors, reviews).mapTo[Event]
  //override def * = (eventId, orgId, name, topic, date, time, price, description, region, visitors, reviews) <> ((Event.apply _).tupled, Event.unapply)

  override def * = (eventId, orgId, name, topic, date, time, price, description, region, visitors, reviews) <> ((Event.apply _).tupled, Event.unapply _)


}

object EventQueryRepository {

  val AllEvents = TableQuery[EventTable]

  def listOrganizerEvents(orgId: Long): DIO[Seq[Event], Effect.Read] =
    AllEvents
      .filter(_.orgId === orgId)
      .result



//    def findEvent(eventId: Long): DIO[Option[Event], Effect.Read] =
//      AllEvents
//        .filter(_.eventId === eventId)
//        .result.headOption
//

  def addEvent(event: Event): DIO[Int, Effect.Write] = {
    AllEvents += event

  }

//  def findEventById(eventId: Long): DIO[Option[Event], Effect.Read] =  {
//    findEvent(eventId)
//  }

  def findEvent(eventId: Long): DIO[Option[Event], Effect.Read] = {
    val query = AllEvents.filter(_.eventId === eventId)
    query.exists.result.flatMap(existingRecord =>
      if (existingRecord) {
        AllEvents
          .filter(_.eventId === eventId)
          .result
          .headOption
      }
      else {
        throw EventNotFoundIdException(eventId)
      }
    )
  }


  def findEventByName(name: String): DIO[Option[Event], Effect.Read] =
    AllEvents
      .filter(_.name === name)
      .result.headOption

  def getIdByEventName(name: String): DIO[Option[Long], Effect.Read]  = {
    findEventByName(name).map(opt => opt match {
      case None => None
      case ev => ev.get.id_event
    })

  }

  def updateNumberUsers(eventId: Long, new_visitors: Int): DIO[Int, Effect.Write] = {
    AllEvents
      .filter(_.eventId === eventId)
      .map(_.visitors)
      .update(new_visitors)

  }

  def countEventsOfOrganizer(orgId: Long): DIO[Int, Effect.Read] =
    AllEvents
      .filter(_.orgId === orgId)
      .length
      .result

  def findEventByDate(date: String): DIO[Seq[Event], Effect.Read] = {
    val query = AllEvents.filter(_.date === date)
    query.exists.result.flatMap(existingRecord =>
      if (existingRecord) {
        AllEvents
          .filter(_.date === date)
          .result
      }
      else {
        throw NoSuchDateException(date)
      }
    )
  }

  def findEventByRegion(region: String): DIO[Seq[Event], Effect.Read] = {
    val query = AllEvents.filter(_.region === region)
    query.exists.result.flatMap(existingRecord =>
      if (existingRecord) {
        AllEvents
          .filter(_.region === region)
          .result
      }
      else {
        throw NoSuchRegionException(region)
      }
    )
  }

//  def findEventByTopic(topic: String): DIO[Seq[Event], Effect.Read] = {
//    val query = AllEvents.filter(_.topic === topic)
//    query.exists.result.flatMap(existingRecord =>
//      if (existingRecord) {
//        AllEvents
//          .filter(_.topic === topic)
//          .result
//      }
//      else {
//        throw NoSuchTopicException(topic)
//      }
//    )
//  }

  def findEventByTopic(topic: String): DIO[Seq[Event], Effect.Read] = {
    AllEvents
      .filter(_.topic === topic)
      .result
  }







}