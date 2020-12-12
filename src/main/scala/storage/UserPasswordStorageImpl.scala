package storage
import org.mindrot.jbcrypt.BCrypt
import tables.UserPasswordQueryRepository

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class UserPasswordStorageImpl extends UserPasswordStorage {

  override def addPassword(userId: Long, password: String): Future[Int] = {
    db.run(UserPasswordQueryRepository.addPassword(userId, password))
  }

  override def checkPassword(userId: Long, password: String): Future[Boolean] = {
    for {
      p <- db.run(UserPasswordQueryRepository.getPassword(userId))
    } yield BCrypt.checkpw(password, p)
  }
}
