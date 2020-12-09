package storage
import tables.{User, UserQueryRepository}

import scala.concurrent.Future

class UserStorageImpl extends UserStorage {

  override def addUser(name: String, email: String, region: String): Future[Int] = {
    db.run(UserQueryRepository.addUser(User(None, name, region, None, email)))
  }

  override def getUserByEmail(email: String): Future[Option[User]] = {
    db.run(UserQueryRepository.findUserByEmail(email))
  }

  override def getUserById(userId: Long): Future[Option[User]] = {
    db.run(UserQueryRepository.findUser(userId))
  }

  override def getTopic(userId: Long): Future[Option[String]] = {
    db.run(UserQueryRepository.getTopics(userId))
  }

  override def updateTopics(userId: Long, new_topics: String): Future[Int] = {
    db.run(UserQueryRepository.updateTopics(userId, new_topics))
  }
}
