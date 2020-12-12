package api

import akka.http.scaladsl.server.Route
import logic.UserService

class UserApi(userService: UserService) {

  import akka.http.scaladsl.server.Directives._
  import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport._

  private val registerUserRoute: Route = (post & path("register" / "user")){
    parameters(Symbol("name").as[String], Symbol("email").as[String], Symbol("region").as[String], Symbol("password").as[String]){
      (name: String, email: String, region: String, password: String) => complete(userService.registerUserByName(name, email, region, password))
    }
  }

  private val registerForEventRoute: Route = (post & path("register" / "event" / LongNumber)){
    userId => parameter(Symbol("eventId").as[Long]){
      (eventId: Long) => complete(userService.registerForEvent(userId, eventId))
    }
  }

  private val getUserRoute: Route = (get & path("get" / "user" / LongNumber)){
    {userId => complete(userService.findUserById(userId))}
  }

  private val getEventRoute: Route = (get & path("get" / "event" / LongNumber)){
    {eventId => complete(userService.findEventById(eventId))}
  }

  private val getAllEventsRegisteredRoute: Route = (get & path("get" / "all" / "user"/ LongNumber)){
    {userId => complete(userService.showAllRegisteredEvents(userId))}
  }

  private val getEventsByDateRoute: Route = (get & path("get" / "events" / "date" )){
    parameters(Symbol("d").as[Int], Symbol("m").as[Int], Symbol("y").as[Int]){(d, m, y) => complete(userService.findEventByDate(y.toString+"-"+m.toString+"-"+d.toString))}
  }

  private val getEventsByRegionRoute: Route = (get & path("get" / "events" / "region" )){
    entity(as[String]){region => complete(userService.findEventByRegion(region))}
  }

  private val getEventsByTopicRoute: Route = (get & path("get" / "events" / "topic" )){
    entity(as[String]){topic => complete(userService.findEventByTopic(topic))}
  }

  private val getRecommendationsRoute: Route = (get & path("get" / "recommendations" / LongNumber )){
    {userId => complete(userService.showRecommendations(userId))}
  }

  private val addTopicRoute: Route = (post & path("add" / "topic" / LongNumber )){userId =>
    entity(as[String]){ topic => complete(userService.addTopic(userId, topic))
    }
  }

  private val checkMyPasswordRoute: Route = (get & path("check" / "password" / LongNumber)) { userId =>
    entity(as[String]) { pswrd => complete(userService.checkPassword(userId, pswrd))
    }
  }

  private val addReviewRoute: Route = (post & path("add" / "review" / LongNumber / LongNumber)) {
    (userId, eventId) => entity(as[String]){
      review => complete(userService.addReview(userId, eventId, review))
    }
  }

  private val getReviewsByEventId: Route = (get & path("get" / "reviews" / "event" / LongNumber )){
    {eventId => complete(userService.getReviewsByEventId(eventId))}
  }

  private val getReviewsByUserId: Route = (get & path("get" / "reviews" / LongNumber )){
    {userId => complete(userService.getReviewsByUserId(userId))}
  }

  private val getNotificationsByUserId: Route = (get & path("get" / "notifications" / LongNumber )){
    {userId => complete(userService.getNotificationsByUserId(userId))}
  }

  val route: Route = registerUserRoute ~ registerForEventRoute ~ getUserRoute ~ getEventRoute ~ getAllEventsRegisteredRoute ~ getEventsByDateRoute ~ getEventsByRegionRoute ~ getEventsByTopicRoute ~ getRecommendationsRoute ~ addTopicRoute ~ checkMyPasswordRoute ~ addReviewRoute ~ getReviewsByEventId ~ getReviewsByUserId ~ getNotificationsByUserId

}
