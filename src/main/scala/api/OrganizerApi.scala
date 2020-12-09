package api

import java.util.UUID

import akka.http.scaladsl.server.Route
import logic.OrganizerService
import slick.jdbc.JdbcBackend.Database
import tables.Organizer

class OrganizerApi(organizerService: OrganizerService) {

  import akka.http.scaladsl.server.Directives._
  import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport._

  private val registerOrganizerRoute: Route = (post & path("register" / "organizer")){
    entity(as[String]){name => complete(organizerService.registerOrganizerByName(name))}
  }

  private val createEventRoute: Route = (post & path("create" / "event" / LongNumber)){orgId =>
    parameters(Symbol("name").as[String], Symbol("topic").as[String], Symbol("date").as[String], Symbol("time").as[String], Symbol("price").as[Int], Symbol("description").as[String], Symbol("region").as[String]){
      (name: String, topic: String, date: String, time: String, price: Int, description: String, region: String) => complete(organizerService.createEvent(orgId, name, topic, date, time, price, description, region))
    }
  }

  private val getOrganizerRoute: Route = (get & path("get" / "organizer" / LongNumber)){
    {orgId => complete(organizerService.findOrganizerById(orgId))}
  }

  private val getEventRoute: Route = (get & path("get" / "event" / LongNumber)){
    {eventId => complete(organizerService.findEventById(eventId))}
  }

  private val getAllCreatedEventsRoute: Route = (get & path("get" / "all" / "events" / "org" / LongNumber)){
    {orgId => complete(organizerService.showAllCreatedEvents(orgId))}
  }

  val route: Route = registerOrganizerRoute ~ createEventRoute ~ getOrganizerRoute ~ getEventRoute ~ getAllCreatedEventsRoute




}
