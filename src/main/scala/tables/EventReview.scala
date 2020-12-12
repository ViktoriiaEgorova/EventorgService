package tables

import api.{UserAlreadyRegisteredForEventException, UserOrEventNotFoundException}
import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import slick.jdbc.H2Profile.api._
import slick.lifted.{ForeignKeyQuery, ProvenShape}
import tables.EventUserQueryRepository.AllEventUsers

import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global

case class EventReview(id_event: Long,
                       id_user: Long,
                       review: String
                      )

object EventReview {
  implicit val jsonDecoder: Decoder[EventReview] = deriveDecoder
  implicit val jsonEncoder: Encoder[EventReview] = deriveEncoder
}


class EventReviewTable(tag: Tag) extends Table[EventReview](tag, "EVENTS_REVIEWS") {

  def eventId: Rep[Long] = column("EVENT_ID")

  def userId: Rep[Long] = column("USER_ID")

  def review: Rep[String] = column("REVIEW")

  override def * : ProvenShape[EventReview] = (eventId, userId, review) <> ((EventReview.apply _).tupled, EventReview.unapply _)


}

object EventReviewQueryRepository {

  val AllEventReviews = TableQuery[EventReviewTable]

  def listEventReviewsByEventId(eventId: Long): DIO[Seq[EventReview], Effect.Read] =
    AllEventReviews
      .filter(_.eventId === eventId)
      .result

  def listEventReviewsByUserId(userId: Long): DIO[Seq[EventReview], Effect.Read] =
    AllEventReviews
      .filter(_.userId === userId)
      .result

  def listEventReviewsByUserIdByEventId(eventId: Long, userId: Long): DIO[Seq[EventReview], Effect.Read] =
    AllEventReviews
      .filter(_.userId === userId)
      .filter(_.eventId === eventId)
      .result


  def addReview(eventId: Long, userId: Long, review: String) = {
    val query = AllEventUsers.filter(_.eventId === eventId).filter(_.userId === userId)
    query.exists.result.flatMap(existingRecord =>
      if (existingRecord) {
        AllEventReviews += EventReview(eventId, userId, review)
      }
      else {
        throw UserOrEventNotFoundException()
      }
    )

  }

  def countReviewForEventByEventId(eventId: Long): DIO[Int, Effect.Read] =
    AllEventReviews
      .filter(_.eventId === eventId)
      .length
      .result

  def countReviewForEventByUserId(userId: Long): DIO[Int, Effect.Read] =
    AllEventReviews
      .filter(_.userId === userId)
      .length
      .result




}