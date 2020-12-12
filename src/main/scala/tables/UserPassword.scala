package tables

import api.{UserAlreadyHavePasswordException, UserAlreadyRegisteredForEventException, UserNotFoundException}
import org.mindrot.jbcrypt.BCrypt
import slick.jdbc.H2Profile.api._
import slick.lifted.{ForeignKeyQuery, ProvenShape}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global

case class UserPassword(id_user: Long, passwrdHash: String )


class UserPasswordTable(tag: Tag) extends Table[UserPassword](tag, "USERS_PASSWORDS") {

  def pswrdHash: Rep[String] = column("PASSWORD")

  def userId: Rep[Long] = column("USER_ID")

  override def * : ProvenShape[UserPassword] = (userId, pswrdHash) <> ((UserPassword.apply _).tupled, UserPassword.unapply _)


}

object UserPasswordQueryRepository {

  val AllUsersPasswords = TableQuery[UserPasswordTable]

  def addPassword(userId: Long, password: String) = {
    val pswrdHash = BCrypt.hashpw(password, BCrypt.gensalt())
    val query = AllUsersPasswords.filter(_.userId === userId)
    query.exists.result.flatMap(existingRecord =>
      if (!existingRecord) {
        AllUsersPasswords += UserPassword(userId, pswrdHash)
      }
      else {
        throw UserAlreadyHavePasswordException()
      }
    )

  }

  def getPassword(userId: Long) = {
    val query = AllUsersPasswords.filter(_.userId === userId)
    query.exists.result.flatMap(existingRecord =>
      if (existingRecord) {
        query.map(up => up.pswrdHash).result.head
      }
      else {
        throw UserNotFoundException(userId)
      }
    )

  }



}
