package logic

import java.sql.SQLException

import api.{EventNotFoundException, EventorgException, NoInterestTopicException, RegistrationForEventException, UnknownEventorgException, UserAlreadyRegisteredException, UserAlreadyRegisteredForEventException, UserRegistrationException}
import storage.{EventReviewStorage, EventStorage, EventUserStorage, UserNotificationStorage, UserPasswordStorage, UserStorage}
import tables.{Event, EventReview, EventUser, User, UserNotification, UserPassword}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class UserServiceImpl(eventStorage: EventStorage, userStorage: UserStorage, eventUserStorage: EventUserStorage, userPasswordStorage: UserPasswordStorage, eventReviewStorage: EventReviewStorage, userNotification: UserNotificationStorage) extends UserService {

//  override def registerUserByName(name: String, email: String, region: String): Future[Option[User]] = {
//
//    val f = for {
//      a <- userStorage.addUser(name, email, region)
//      b <- userStorage.getUserByEmail(email)
//    } yield b
//
//    f.recover {
//      case ex: SQLException => throw UserAlreadyRegisteredException(email)
//      //case ex: SQLException => throw UserRegistrationException(ex.getMessage)
//    }
//
//  }


  override def registerForEvent(userId: Long, eventId: Long): Future[Int] = {
    val f = for {
      _ <- eventUserStorage.addEvent(eventId, userId)
      num <- eventUserStorage.countVisitorsForEvent(eventId)
      _ <-  eventStorage.updateNumberVisitors(eventId, num)
      result <- eventUserStorage.countVisitorsForEvent(eventId)
    } yield result

    f.recover{
      case ex: UserAlreadyRegisteredForEventException => throw UserAlreadyRegisteredForEventException(eventId, userId)
      case ex: SQLException => throw RegistrationForEventException(eventId)
    }
  }


  override def findUserById(userId: Long): Future[Option[User]] = {
    userStorage.getUserById(userId)
  }


  override def findEventById(eventId: Long): Future[Option[Event]] = {
    eventStorage.getEventById(eventId)
  }


  override def showAllRegisteredEvents(userId: Long): Future[Seq[Option[Event]]] = {
    val f = eventUserStorage.listEventUsersByUserId(userId)
    f.flatMap(s => Future.sequence(s.map(l => findEventById(l))))
  }


  override def findEventByTopic(topic: String): Future[Seq[Event]] = {
    eventStorage.getEventByTopic(topic)
  }


  override def findEventByRegion(region: String): Future[Seq[Event]] = {
    eventStorage.getEventByRegion(region)
  }


  override def findEventByDate(date: String): Future[Seq[Event]] = {
    eventStorage.getEventByDate(date)
  }


  override def showRecommendations(userId: Long): Future[Seq[Event]] = {
    val topics = userStorage.getTopic(userId)
    topics.flatMap(opt => opt match {
      case None =>throw NoInterestTopicException()
      case Some(str) => {
        val seq = str.split(",").toSeq
        val f = for {
          topic <- seq
        } yield findEventByTopic(topic)
        Future.sequence(f) map {
          _.flatten
        }
      }
    }
    )
  }

  override def addTopic(userId: Long, topic: String): Future[Option[User]] = {
    val topics = userStorage.getTopic(userId)
    val q: Future[Int] = topics.map(opt => opt match {
      case Some(str) => {
        val new_str = str + "," + topic
        userStorage.updateTopics(userId, new_str)
      }
      case None => userStorage.updateTopics(userId, topic)
    }).flatten

    val f = for {
      a <- q
      b <- userStorage.getUserById(userId)
    } yield b

    f.recover {
      case ex: SQLException => throw UnknownEventorgException(ex.getMessage)
      //case ex: SQLException => throw UserRegistrationException(ex.getMessage)
    }


  }

  override def registerUserByName(name: String, email: String, region: String, password: String): Future[Option[User]] = {
    val f = for {
      a <- userStorage.addUser(name, email, region)
      d <- userStorage.getUserByEmail(email)
      c <- userPasswordStorage.addPassword(d.get.id_user.get, password)
      b <- userStorage.getUserByEmail(email)
    } yield b

    f.recover {
      case ex: SQLException => throw UserAlreadyRegisteredException(email)
      //case ex: SQLException => throw UserRegistrationException(ex.getMessage)
    }
  }

  override def checkPassword(userId: Long, password: String): Future[Boolean] = {
    userPasswordStorage.checkPassword(userId, password)
  }


  override def getReviewsByUserId(userId: Long): Future[Seq[EventReview]] = {
    eventReviewStorage.listReviewsByUserId(userId)
  }


  override def getReviewsByEventId(eventId: Long): Future[Seq[EventReview]] = {
    eventReviewStorage.listReviewsByEventId(eventId)
  }


  override def addReview(eventId: Long, userId: Long, review: String): Future[Seq[EventReview]] = {
    val f = for {
      _ <- eventReviewStorage.addEventReview(eventId, userId, review)
      num <- eventReviewStorage.listReviewsByEventId(eventId)
      _ <- eventStorage.updateNumberReviews(eventId, num.length)
      result <- eventReviewStorage.listReviewsByUserId(userId)
    } yield result

    f.recover{
      //case ex: UserAlreadyRegisteredForEventException => throw UserAlreadyRegisteredForEventException(eventId, userId)
      case ex: SQLException => throw UnknownEventorgException(ex.getMessage)
    }
  }

  override def getNotificationsByUserId(userId: Long): Future[Seq[UserNotification]] = {
    userNotification.listNotificationsByUserId(userId)
  }
}
