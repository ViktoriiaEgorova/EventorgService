package storage

import tables.{EventReview, UserNotification}

import scala.concurrent.Future

/**
 * Хранилище отношейний событие-юзер-отзыв - работа с таблицей EventUserTable
 */


trait UserNotificationStorage {

  def listAllUsers: Future[Seq[Option[Long]]]

  def addUserNotification(userId: Long, review: String): Future[Int]

  def countNotificationsByUserId(userId: Long): Future[Int]

  def listNotificationsByUserId(userId: Long): Future[Seq[UserNotification]]


}