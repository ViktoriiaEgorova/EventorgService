package storage
import tables.{Event, EventQueryRepository}

import scala.concurrent.Future

class EventStorageImpl extends EventStorage {

  override def addEvent(orgId: Long, name: String, topic: String, date: String, time: String, price: Int, description: String, region: String): Future[Int] = {
    db.run(EventQueryRepository.addEvent(Event(None, orgId, name, topic, date, time, price, description, region )))
  }

  override def getEventByName(name: String): Future[Option[Event]] = {
    db.run(EventQueryRepository.findEventByName(name))
  }

  override def getEventById(eventId: Long): Future[Option[Event]] = {
    db.run(EventQueryRepository.findEvent(eventId))
  }

  override def getEventIdByEventName(name: String): Future[Option[Long]] = {
    db.run(EventQueryRepository.getIdByEventName(name))
  }

  override def updateNumberVisitors(eventId: Long, new_visitors: Int): Future[Int] = {
    db.run(EventQueryRepository.updateNumberUsers(eventId, new_visitors))
  }

  override def getAllEventsByOrgId(orgId: Long): Future[Seq[Event]] = {
    db.run(EventQueryRepository.listOrganizerEvents(orgId))
  }

  override def getEventByTopic(topic: String): Future[Seq[Event]] = {
    db.run(EventQueryRepository.findEventByTopic(topic))
  }

  override def getEventByRegion(region: String): Future[Seq[Event]] = {
    db.run(EventQueryRepository.findEventByRegion(region))
  }

  override def getEventByDate(date: String): Future[Seq[Event]] = {
    db.run(EventQueryRepository.findEventByDate(date))
  }


}
