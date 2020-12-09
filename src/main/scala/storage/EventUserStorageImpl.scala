package storage
import tables.{EventUser, EventUserQueryRepository}

import scala.concurrent.Future

class EventUserStorageImpl extends EventUserStorage {

//  override def addEvent(eventId: Long, userId: Long): Future[Int] = {
//    db.run(EventUserQueryRepository.addEventUser(eventId, userId))
//  }

  override def addEvent(eventId: Long, userId: Long): Future[Int] = {
    db.run(EventUserQueryRepository.addEventUser(eventId, userId))
  }

  override def countVisitorsForEvent(eventId: Long): Future[Int] = {
    db.run(EventUserQueryRepository.countVisitorsForEvent(eventId))
  }

  override def listEventUsersByUserId(userId: Long): Future[Seq[Long]] = {
    db.run(EventUserQueryRepository.listEventUsersByUserId(userId))
  }
}
