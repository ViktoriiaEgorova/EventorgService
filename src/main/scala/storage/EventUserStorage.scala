package storage

import tables.EventUser

import scala.concurrent.Future

/**
 * Хранилище отношейний юзер-событие - работа с таблицей EventUserTable
 */


trait EventUserStorage {

  def addEvent(eventId: Long, userId: Long): Future[Int]

  def countVisitorsForEvent(eventId: Long): Future[Int]

  def listEventUsersByUserId(userId: Long): Future[Seq[Long]]


}
