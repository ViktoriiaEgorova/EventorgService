package tables

import api.{UserAlreadyRegisteredForEventException, UserOrEventNotFoundException}
import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import slick.jdbc.H2Profile.api._
import slick.lifted.{ForeignKeyQuery, ProvenShape}
import tables.EventUserQueryRepository.AllEventUsers
import tables.UserQueryRepository.AllUsers

import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global

case class UserNotification(id_user: Long,
                            notification: String
                           )

object UserNotification {
  implicit val jsonDecoder: Decoder[UserNotification] = deriveDecoder
  implicit val jsonEncoder: Encoder[UserNotification] = deriveEncoder
}


class UserNotificationTable(tag: Tag) extends Table[UserNotification](tag, "USER_NOTIFICATION") {

  def userId: Rep[Long] = column("USER_ID")

  def notification: Rep[String] = column("NOTIFICATION")

  override def * : ProvenShape[UserNotification] = (userId, notification) <> ((UserNotification.apply _).tupled, UserNotification.unapply _)


}

object UserNotificationQueryRepository {

  val AllUserNotifications = TableQuery[UserNotificationTable]

  def listUserNotificationByUserId(userId: Long): DIO[Seq[UserNotification], Effect.Read] =
    AllUserNotifications
      .filter(_.userId === userId)
      .result

  def addNotification(userId: Long, notification: String) = {
    AllUserNotifications += UserNotification(userId, notification)

  }


  def countNotificationsByUserId(userId: Long): DIO[Int, Effect.Read] =
    AllUserNotifications
      .filter(_.userId === userId)
      .length
      .result

  def listAllUserIds: DIO[Seq[Option[Long]], Effect.Read] =
    AllUsers.map(t  => t.userId).result




}