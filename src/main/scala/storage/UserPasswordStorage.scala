package storage

import scala.concurrent.Future

trait UserPasswordStorage {

  def addPassword(userId: Long, password: String): Future[Int]

  def checkPassword(userId: Long, password: String): Future[Boolean]

}