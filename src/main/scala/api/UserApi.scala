package api

import akka.http.scaladsl.server.Route
import logic.UserService

class UserApi(userService: UserService) {

  import akka.http.scaladsl.server.Directives._
  import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport._

  private val registerUserRoute: Route = (post & path("register" / "user")){
    parameters(Symbol("name").as[String], Symbol("email").as[String], Symbol("region").as[String]){
      (name: String, email: String, region: String) => complete(userService.registerUserByName(name, email, region))
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
    entity(as[String]){date => complete(userService.findEventByDate(date))}
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

  val route: Route = registerUserRoute ~ registerForEventRoute ~ getUserRoute ~ getEventRoute ~ getAllEventsRegisteredRoute ~ getEventsByDateRoute ~ getEventsByRegionRoute ~ getEventsByTopicRoute ~ getRecommendationsRoute ~ addTopicRoute

}
