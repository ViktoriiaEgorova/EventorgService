package storage

import tables.{User}

import scala.concurrent.Future

/**
 * Хранилище пользователей - работа с таблицей UserTable
 */

trait UserStorage {

  def addUser(name: String, email: String, region: String): Future[Int]

  def getUserByEmail(email: String): Future[Option[User]]

  def getUserById(userId: Long): Future[Option[User]]

  def getTopic(userId: Long): Future[Option[String]]

  def updateTopics(userId: Long, new_topics: String): Future[Int]

  def sendNotificationToAllUsers(orgId: Long): Future[Int]


}
