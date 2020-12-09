package tables

import api.UserAlreadyRegisteredForEventException
import slick.jdbc.H2Profile.api._
import slick.lifted.{ForeignKeyQuery, ProvenShape}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global

case class EventUser(id_event: Long,
                     id_user: Long
                    )


class EventUserTable(tag: Tag) extends Table[EventUser](tag, "EVENTS_USERS") {

  def eventId: Rep[Long] = column("EVENT_ID")

  def userId: Rep[Long] = column("USER_ID")

  override def * : ProvenShape[EventUser] = (eventId, userId) <> ((EventUser.apply _).tupled, EventUser.unapply _)


}

object EventUserQueryRepository {

  val AllEventUsers = TableQuery[EventUserTable]

  def listEventUsersByEventId(eventId: Long): DIO[Seq[EventUser], Effect.Read] =
    AllEventUsers
      .filter(_.eventId === eventId)
      .result

  def listEventUsersByUserId(userId: Long): DIO[Seq[Long], Effect.Read] =
    AllEventUsers
      .filter(_.userId === userId)
      .map(t => t.eventId)
      .result




  //  def findEvent(eventId: Long): DIO[Option[Event], Effect.Read] =
//    AllEvents
//      .filter(_.eventId === eventId)
//      .result.headOption


//  def addEventUser(eventId: Long, userId: Long): DIO[Int, Effect.Write] = {
//    AllEventUsers += EventUser(eventId, userId)
//
//  }

//: DIO[Int, Effect.Write]
  def addEventUser(eventId: Long, userId: Long) = {
    val query = AllEventUsers.filter(_.eventId === eventId).filter(_.userId === userId)
    query.exists.result.flatMap(existingRecord =>
      if (!existingRecord) {
        AllEventUsers += EventUser(eventId, userId)
        }
      else {
        throw UserAlreadyRegisteredForEventException(eventId, userId)
      }
    )
    //AllEventUsers += EventUser(eventId, userId)

  }

  def countVisitorsForEvent(eventId: Long): DIO[Int, Effect.Read] =
    AllEventUsers
      .filter(_.eventId === eventId)
      .length
      .result


}