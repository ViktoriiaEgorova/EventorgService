package storage
import scala.concurrent.Future
import tables.{EventReview, EventReviewQueryRepository}

class EventReviewStorageImpl extends EventReviewStorage {

  override def addEventReview(eventId: Long, userId: Long, review: String): Future[Int] = {
    db.run(EventReviewQueryRepository.addReview(eventId, userId, review))
  }

  override def countReviewsForEventByEventId(eventId: Long): Future[Int] = {
    db.run(EventReviewQueryRepository.countReviewForEventByEventId(eventId))
  }

  override def listReviewsByEventId(eventId: Long): Future[Seq[EventReview]] = {
    db.run(EventReviewQueryRepository.listEventReviewsByEventId(eventId))
  }

  override def listReviewsByUserId(userId: Long): Future[Seq[EventReview]] = {
    db.run(EventReviewQueryRepository.listEventReviewsByUserId(userId))
  }

  override def countReviewsForEventByUserId(userId: Long): Future[Int] = {
    db.run(EventReviewQueryRepository.countReviewForEventByUserId(userId))
  }


}
