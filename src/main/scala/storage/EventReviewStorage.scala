package storage

import tables.EventReview

import scala.concurrent.Future

/**
 * Хранилище отношейний событие-юзер-отзыв - работа с таблицей EventUserTable
 */


trait EventReviewStorage {

  def addEventReview(eventId: Long, userId: Long, review: String): Future[Int]

  def countReviewsForEventByEventId(eventId: Long): Future[Int]

  def countReviewsForEventByUserId(userId: Long): Future[Int]

  def listReviewsByEventId(eventId: Long): Future[Seq[EventReview]]

  def listReviewsByUserId(userId: Long): Future[Seq[EventReview]]


}