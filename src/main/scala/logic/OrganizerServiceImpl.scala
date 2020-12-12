package logic

import java.sql.SQLException

import api.{CreateEventException, EventAlreadyExistsException, OrganizerAlreadyRegisteredException, OrganizerRegistrationException, EventorgException}
import storage.{EventStorage, OrganizerStorage, UserNotificationStorage}
import tables.{Event, Organizer}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class OrganizerServiceImpl(eventStorage: EventStorage, organizerStorage: OrganizerStorage, userNotificationStorage: UserNotificationStorage) extends OrganizerService {

  override def registerOrganizerByName(name: String): Future[Option[Organizer]] = {

    val f = for {
      a <- organizerStorage.addOrganizer(name)
      b <- organizerStorage.getOrganizerByName(name)
    } yield b

    f.recover {
      case ex: SQLException => throw OrganizerAlreadyRegisteredException(name)
      //case ex: SQLException => throw OrganizerRegistrationException(ex.getMessage)
      }

  }

//  override def createEvent(orgId: Long, name: String, topic: String, date: String, time: String, price: Int, description: String, region: String): Future[Option[Event]] = {
//
//    val f = for {
//      a <- eventStorage.addEvent(orgId, name, topic, date, time, price, description, region)
//      b <- eventStorage.getEventByName(name)
//    } yield b
//
//    f.recover {
//      case ex: SQLException => throw EventAlreadyExistsException(name)
//      //case ex: SQLException => throw CreateEventException(ex.getMessage)
//    }
//  }

  override def createEvent(orgId: Long, name: String, topic: String, date: String, time: String, price: Int, description: String, region: String): Future[Option[Event]] = {

        val f = for {
          a <- eventStorage.addEvent(orgId, name, topic, date, time, price, description, region)
          num <- organizerStorage.countEventsOfOrganizer(orgId)
          _ <-  organizerStorage.updateNumberEvents(orgId, num)
          d <- sendNotificationToAll(orgId)
          b <- eventStorage.getEventByName(name)
        } yield b

        f.recover {
          case ex: SQLException => throw EventAlreadyExistsException(name)
          //case ex: SQLException => throw CreateEventException(ex.getMessage)
        }
      }


  override def findOrganizerById(orgId: Long): Future[Option[Organizer]] = {
    organizerStorage.getOrganizerById(orgId)
  }


  override def findEventById(eventId: Long): Future[Option[Event]] = {
    eventStorage.getEventById(eventId)
  }

  override def showAllCreatedEvents(orgId: Long): Future[Seq[Event]] = {
    eventStorage.getAllEventsByOrgId(orgId)

  }

  override def sendNotificationToAll(orgId: Long): Future[Int] = {
    for {
      seqid <- userNotificationStorage.listAllUsers
    } yield seqid.map(opt => opt match {
      case Some(id) => userNotificationStorage.addUserNotification(id, "Organizer with id=" + orgId + " have created new event")
    }).length
  }


}
