import java.util.UUID

import akka.actor.ActorSystem
import akka.http.scaladsl.server.{Route, RouteConcatenation}
import api.{EventorgExceptionHandler, OrganizerApi, UserApi}
import logic.{OrganizerService, OrganizerServiceImpl, UserService, UserServiceImpl}
import slick.jdbc.JdbcBackend.Database
import storage.{EventReviewStorage, EventReviewStorageImpl, EventStorage, EventStorageImpl, EventUserStorage, EventUserStorageImpl, OrganizerStorage, OrganizerStorageImpl, UserNotificationStorage, UserNotificationStorageImpl, UserPasswordStorage, UserPasswordStorageImpl, UserStorage, UserStorageImpl}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import akka.http.scaladsl.Http
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

object EventorgHttpApp {
  implicit val ac: ActorSystem = ActorSystem()
  implicit val ec: ExecutionContext = ac.dispatcher

  def main(args: Array[String]): Unit = {
    Await.result(Eventorg().start(), Duration.Inf)
  }
}


case class Eventorg()(implicit ac: ActorSystem, ec: ExecutionContext) extends LazyLogging {

  private val eventStorage: EventStorage = new EventStorageImpl
  private val userStorage: UserStorage = new UserStorageImpl
  private val organizerStorage: OrganizerStorage = new OrganizerStorageImpl
  private val eventUserStorage: EventUserStorage = new EventUserStorageImpl
  private val userPasswordStorage: UserPasswordStorage = new UserPasswordStorageImpl
  private val eventReviewStorage: EventReviewStorage = new EventReviewStorageImpl
  private val userNotificationStorage: UserNotificationStorage = new UserNotificationStorageImpl
  private val organizerService: OrganizerService = new OrganizerServiceImpl(eventStorage, organizerStorage, userNotificationStorage)
  private val userService: UserService = new UserServiceImpl(eventStorage, userStorage, eventUserStorage, userPasswordStorage, eventReviewStorage, userNotificationStorage) // или (organizerService)
  private val userRoute: UserApi = new UserApi(userService)
  private val organizerRoute: OrganizerApi = new OrganizerApi(organizerService)
  private val routes = Route.seal(RouteConcatenation.concat(userRoute.route, organizerRoute.route))(exceptionHandler = EventorgExceptionHandler.exceptionHandler)

  def start(): Future[Http.ServerBinding] =
    Http()
      .newServerAt("localhost", 8080)
      .bind(routes)
      .andThen { case b => logger.info(s"server started at: $b") }
}