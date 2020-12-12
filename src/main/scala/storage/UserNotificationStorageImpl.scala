package storage

import tables.{UserNotification, UserNotificationQueryRepository}

import scala.concurrent.Future

class UserNotificationStorageImpl extends UserNotificationStorage {

  override def listAllUsers: Future[Seq[Option[Long]]] = {
    db.run(UserNotificationQueryRepository.listAllUserIds)
  }

  override def addUserNotification(userId: Long, notif: String): Future[Int] = {
    db.run(UserNotificationQueryRepository.addNotification(userId, notif))
  }

  override def countNotificationsByUserId(userId: Long): Future[Int] = {
    db.run(UserNotificationQueryRepository.countNotificationsByUserId(userId))
  }

  override def listNotificationsByUserId(userId: Long): Future[Seq[UserNotification]] = {
    db.run(UserNotificationQueryRepository.listUserNotificationByUserId(userId))
  }
}
