package tables

import api.{NoInterestTopicException, UserNotFoundException}
import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import slick.dbio.{DBIO, DBIOAction, Effect, NoStream}
import slick.jdbc.H2Profile.api._
import slick.lifted.{ForeignKeyQuery, ProvenShape, TableQuery}

import scala.concurrent.ExecutionContext.Implicits.global

case class User(id_user: Option[Long],
                name: String,
                region: String,
                topics: Option[String] = None,
                email: String,
                events: Option[Int] = None
               )

object User {
  implicit val jsonDecoder: Decoder[User] = deriveDecoder
  implicit val jsonEncoder: Encoder[User] = deriveEncoder
}

class UserTable(tag: Tag) extends Table[User](tag, "USERS") {

  def userId: Rep[Option[Long]] = column("USER_ID", O.PrimaryKey, O.AutoInc)

  def name: Rep[String] = column("USER_NAME")

  def region: Rep[String] = column("USER_REGION")

  def topics: Rep[Option[String]] = column("USER_TOPICS")

  def email: Rep[String] = column("EMAIL", O.Unique)

  def events: Rep[Option[Int]] = column("USER_EVENTS")

  //  def organizer: ForeignKeyQuery[OrganizerTable, Organizer] =
  //    foreignKey("ORGINIZER_FK", orgId, OrganizerQueryRepository.AllCustomers)(_.id)

  //override def * : ProvenShape[User] = (userId, name, region, topics, email, events).mapTo[User]
  override def * = (userId, name, region, topics, email, events) <> ((User.apply _).tupled, User.unapply)


}

object UserQueryRepository {

  val AllUsers = TableQuery[UserTable]

  def addUser(user: User): DIO[Int, Effect.Write] =
    AllUsers += user

//  def findUser(id: Long): DIO[Option[User], Effect.Read] =
//    AllUsers
//      .filter(_.userId === id)
//      .result
//      .headOption

  def findUser(userId: Long): DIO[Option[User], Effect.Read] = {
    val query = AllUsers.filter(_.userId === userId)
    query.exists.result.flatMap(existingRecord =>
      if (existingRecord) {
        AllUsers
          .filter(_.userId === userId)
          .result
          .headOption
      }
      else {
        throw UserNotFoundException(userId)
      }
    )

  }

  def findUserByEmail(email: String): DIO[Option[User], Effect.Read] =
    AllUsers
      .filter(_.email === email)
      .result
      .headOption

  def getTopics(userId: Long): DIO[Option[String], Effect.Read] = {
    val query = AllUsers.filter(_.userId === userId)
    query.exists.result.flatMap(existingRecord =>
      if (existingRecord) {
        AllUsers
          .filter(_.userId === userId)
          .map(t => t.topics)
          .result
          .headOption
          .map(_.flatten)
      }
      else {
        throw NoInterestTopicException()
      }
    )
  }

  def updateTopics(userId: Long, new_topics: String): DIO[Int, Effect.Write] = {
    AllUsers
      .filter(_.userId === userId)
      .map(_.topics)
      .update(Some(new_topics))

  }




}
