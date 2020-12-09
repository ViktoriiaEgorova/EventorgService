package api

import akka.http.scaladsl.model.StatusCodes.BadRequest
import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.ExceptionHandler

sealed abstract class EventorgException(message: String) extends Exception(message)

final case class EventNotFoundException(name: String) extends EventorgException(s"Event with name=$name not found")

final case class UserRegistrationException(msg: String) extends EventorgException(s"Some problems with user registration: " + msg)

final case class OrganizerRegistrationException(msg: String) extends EventorgException(s"Some problems with organizer registration: " + msg)

final case class UserAlreadyRegisteredException(email: String) extends EventorgException(s"User with email=$email already exists")

final case class UserNotRegisteredException(email: String) extends EventorgException(s"User with email=$email is not registered")

final case class RegistrationForEventException(eventId: Long) extends EventorgException(s"Some problems with registration to the event $eventId")

final case class CreateEventException(msg: String) extends EventorgException(s"Some problems during creation of event:" + msg)

final case class UserAlreadyRegisteredForEventException(eventId: Long, userId: Long) extends EventorgException(s"User with id=$userId already registered for event with id=$eventId")

final case class OrganizerAlreadyRegisteredException(name: String) extends EventorgException(s"Organizer with name=$name already registered")

final case class EventAlreadyExistsException(name: String) extends EventorgException(s"Event with name=$name already exists")

final case class OrganizerAlreadyCreatedEventException(eventId: Long, orgId: Long) extends EventorgException(s"Organizer with id=$orgId have already created an event with id=$eventId")

final case class OrganizerNotFoundException(orgId: Long) extends EventorgException(s"Organizer with id=$orgId not found")

final case class UserNotFoundException(userId: Long) extends EventorgException(s"User with id=$userId not found")

final case class EventNotFoundIdException(eventId: Long) extends EventorgException(s"Event with id=$eventId not found")

final case class NoSuchDateException(date: String) extends EventorgException(s"No events at date=$date")

final case class NoSuchRegionException(region: String) extends EventorgException(s"No events at region=$region")

final case class NoSuchTopicException(topic: String) extends EventorgException(s"No events with topic=$topic")

final case class NoInterestTopicException() extends EventorgException(s"You are not interested in any topics")

final case class UnknownEventorgException(msg: String) extends EventorgException(msg)


object EventorgExceptionHandler {
  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._

  val exceptionHandler: ExceptionHandler =
    ExceptionHandler {
      case e: EventorgException => complete(BadRequest, ExceptionResponse(e.getMessage))
    }
}