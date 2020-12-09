package api

import akka.http.scaladsl.testkit.ScalatestRouteTest
import logic.OrganizerService
import org.scalamock.scalatest.MockFactory
import org.scalatest.funspec.AnyFunSpec
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.model.StatusCodes
import tables.{Organizer, Event}

import scala.concurrent.Future

class OrganizerApiSpec extends AnyFunSpec with ScalatestRouteTest with MockFactory {

  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._

  describe("POST /register/organizer") {

    it("регистрирует организатора по имени и возвращает Option[Organizer") {
      (mockOrganizerService.registerOrganizerByName _)
        .expects("Sample_organizer")
        .returns(Future.successful(Some(sampleOrganizer)))

      Post("/register/organizer", "Sample_organizer") ~> route ~> check {
        assert(status == StatusCodes.OK)
        assert(responseAs[Option[Organizer]].contains(sampleOrganizer))
      }
    }

    it("возвращает код 400 и текст ошибки в виде json, если организатор с таким именем уже зарегистрирован") {
      (mockOrganizerService.registerOrganizerByName _)
        .expects("Sample_organizer")
        .returns(Future.failed(OrganizerAlreadyRegisteredException("Sample organizer")))

      Post("/register/organizer", "Sample_organizer") ~> route ~> check {
        assert(status == StatusCodes.BadRequest)
        assert(responseAs[ExceptionResponse] == ExceptionResponse("Organizer with name=Sample organizer already registered"))
      }
    }
  }

  describe("POST http://localhost:8080/create/event/{orgId}/orgId?name={name}&topic={topic}&date={date}&time={time}&price={price}&description={description}&region={region") {
    it("возвращает созданное мероприятие") {
      (mockOrganizerService.createEvent _)
        .expects(1, "Sample_event_name","business","2020-11-13","19:00",100, "Sample_event_description", "Saint-Petersburg")
        .returns(Future.successful(Some(sampleEvent)))

      Post("/create/event/1?name=Sample_event_name&topic=business&date=2020-11-13&time=19:00&price=100&description=Sample_event_description&region=Saint-Petersburg") ~> route ~> check {
        assert(status == StatusCodes.OK)
        assert(responseAs[Event] == sampleEvent)
      }
    }
  }

  describe("GET http://localhost:8080/get/organizer/{orgId}") {

    it("возвращает организатора по id") {
      (mockOrganizerService.findOrganizerById _)
        .expects(1)
        .returns(Future.successful(Some(sampleOrganizer)))

      Get("/get/organizer/1") ~> route ~> check {
        assert(status == StatusCodes.OK)
        assert(responseAs[Option[Organizer]].contains(sampleOrganizer))
      }
    }

    it("возвращает код 400 и предупреждение, если организатор не зарегистрирован") {
      (mockOrganizerService.findOrganizerById _)
        .expects(2)
        .returns(Future.failed(OrganizerNotFoundException(2)))

      Get("/get/organizer/2") ~> route ~> check {
        assert(status == StatusCodes.BadRequest)
        assert(responseAs[ExceptionResponse] == ExceptionResponse(s"Organizer with id=2 not found"))
      }
    }

  }

  describe("GET http://localhost:8080/get/event/{eventId}") {

    it("возвращает мероприятие по id") {
      (mockOrganizerService.findEventById _)
        .expects(1)
        .returns(Future.successful(Some(sampleEvent)))

      Get("/get/event/1") ~> route ~> check {
        assert(status == StatusCodes.OK)
        assert(responseAs[Option[Event]].contains(sampleEvent))
      }
    }

    it("возвращает код 400 и предупреждение, если мероприятие не найдено") {
      (mockOrganizerService.findEventById _)
        .expects(2)
        .returns(Future.failed(EventNotFoundIdException(2)))

      Get("/get/event/2") ~> route ~> check {
        assert(status == StatusCodes.BadRequest)
        assert(responseAs[ExceptionResponse] == ExceptionResponse(s"Event with id=2 not found"))
      }
    }

  }

  describe("GET http://localhost:8080/get/all/events/org/{orgId}") {

    it("возвращает все созданные организатором мероприятия") {
      (mockOrganizerService.showAllCreatedEvents _)
        .expects(1)
        .returns(Future.successful(Seq(sampleEvent, sampleEvent2)))

      Get("/get/all/events/org/1") ~> route ~> check {
        assert(status == StatusCodes.OK)
        assert(responseAs[Seq[Event]] == (Seq(sampleEvent, sampleEvent2)))
      }
    }

  }


  private val mockOrganizerService = mock[OrganizerService]
  private val route = {
    Route.seal(
      new OrganizerApi(mockOrganizerService).route
    )(exceptionHandler = EventorgExceptionHandler.exceptionHandler)
  }

  private val sampleEvent = tables.Event(
    id_event = Some(1),
    id_org = 1,
    name = "Sample_event_name",
    topic = "business",
    date = "2020-11-13",
    time = "19:00",
    price = 100,
    description = "Sample_event_description",
    region = "Saint-Petersburg"
  )

  private val sampleEvent2 = tables.Event(
    id_event = Some(2),
    id_org = 1,
    name = "Sample_event_name_2",
    topic = "business",
    date = "2020-11-13",
    time = "19:00",
    price = 100,
    description = "Sample_event_description",
    region = "Saint-Petersburg"
  )

  private val sampleOrganizer = tables.Organizer(
    id_org = Some(1),
    name = "Sample_organizer",
    events = None
  )

}
