package api

import akka.http.scaladsl.testkit.ScalatestRouteTest
import logic.UserService
import org.scalamock.scalatest.MockFactory
import org.scalatest.funspec.AnyFunSpec
import akka.http.scaladsl.model.StatusCodes
import tables.{Event, User, EventUser}
import akka.http.scaladsl.server.Route
import api.EventorgExceptionHandler
import api.EventorgException

import scala.concurrent.Future

class UserApiSpec extends AnyFunSpec with ScalatestRouteTest with MockFactory {

  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._

  describe("POST /register/user?name={name}&email={email}&region={region}") {

    it("регистрирует юзера и возвращает созданного пользователя") {
      (mockUserService.registerUserByName _)
        .expects("Vasya", "email", "Saint-Petersburg")
        .returns(Future.successful(Some(sampleUser)))

      Post("/register/user?name=Vasya&email=email&region=Saint-Petersburg") ~> route ~> check {
        assert(status == StatusCodes.OK)
        assert(responseAs[Option[User]].contains(sampleUser))
      }
    }

  }

  describe("POST http://localhost:8080/register/event/{userId}?eventId={eventId}") {
    it("возвращает новое число зарегистрировавшихся на мероприятие пользователей") {
      (mockUserService.registerForEvent _)
        .expects(1, 1)
        .returns(Future.successful(1))


      Post("/register/event/1?eventId=1") ~> route ~> check {
        assert(status == StatusCodes.OK)
        assert(responseAs[Int] == 1)
      }
    }
  }

  describe("GET http://localhost:8080/get/user/{userId}") {

    it("возвращает пользователя по id") {
      (mockUserService.findUserById _)
        .expects(1)
        .returns(Future.successful(Some(sampleUser)))

      Get("/get/user/1") ~> route ~> check {
        assert(status == StatusCodes.OK)
        assert(responseAs[Option[User]].contains(sampleUser))
      }
    }

    it("возвращает код 400 и предупреждение, если пользователь не зарегистрирован") {
      (mockUserService.findUserById _)
        .expects(2)
        .returns(Future.failed(UserNotFoundException(2)))

      Get("/get/user/2") ~> route ~> check {
        assert(status == StatusCodes.BadRequest)
        assert(responseAs[ExceptionResponse] == ExceptionResponse(s"User with id=2 not found"))
      }
    }

  }

  describe("GET http://localhost:8080/get/event/{eventId}") {

    it("возвращает мероприятие по id") {
      (mockUserService.findEventById _)
        .expects(1)
        .returns(Future.successful(Some(sampleEvent)))

      Get("/get/event/1") ~> route ~> check {
        assert(status == StatusCodes.OK)
        assert(responseAs[Option[Event]].contains(sampleEvent))
      }
    }

    it("возвращает код 400 и предупреждение, если мероприятие не найдено") {
      (mockUserService.findEventById _)
        .expects(2)
        .returns(Future.failed(EventNotFoundIdException(2)))

      Get("/get/event/2") ~> route ~> check {
        assert(status == StatusCodes.BadRequest)
        assert(responseAs[ExceptionResponse] == ExceptionResponse(s"Event with id=2 not found"))
      }
    }

  }


  describe("GET http://localhost:8080/get/all/user/{userId}") {

    it("возвращает все мероприятия, на которые зарегистрирован пользователь") {
      (mockUserService.showAllRegisteredEvents _)
        .expects(1)
        .returns(Future.successful(Seq(Some(sampleEvent))))

      Get("/get/all/user/1") ~> route ~> check {
        assert(status == StatusCodes.OK)
        assert(responseAs[Seq[Option[Event]]].contains(Some(sampleEvent)))
      }
    }

  }

  describe("GET http://localhost:8080/get/events/date?date=") {

    it("возвращает все мероприятия с указанной датой") {
      (mockUserService.findEventByDate _)
        .expects("2020-11-13")
        .returns(Future.successful(Seq(sampleEvent)))

      Get("/get/events/date?date=", "2020-11-13") ~> route ~> check {
        assert(status == StatusCodes.OK)
        assert(responseAs[Seq[Event]] == (Seq(sampleEvent)))
      }
    }

    it("возвращает код 400 и предупреждение, если мероприятий с данной датой нет") {
      (mockUserService.findEventByDate _)
        .expects("2020-11-15")
        .returns(Future.failed(NoSuchDateException("2020-11-15")))

      Get("/get/events/date?date=", "2020-11-15") ~> route ~> check {
        assert(status == StatusCodes.BadRequest)
        assert(responseAs[ExceptionResponse] == ExceptionResponse("No events at date=2020-11-15"))
      }
    }
  }

  describe("GET http://localhost:8080/get/events/region") {

    it("возвращает все мероприятия с указанным регионом") {
      (mockUserService.findEventByRegion _)
        .expects("Saint-Petersburg")
        .returns(Future.successful(Seq(sampleEvent)))

      Get("/get/events/region", "Saint-Petersburg") ~> route ~> check {
        assert(status == StatusCodes.OK)
        assert(responseAs[Seq[Event]] == (Seq(sampleEvent)))
      }
    }

    it("возвращает код 400 и предупреждение, если мероприятий в данном регионе нет") {
      (mockUserService.findEventByRegion _)
        .expects("Moscow")
        .returns(Future.failed(NoSuchRegionException("Moscow")))

      Get("/get/events/region", "Moscow") ~> route ~> check {
        assert(status == StatusCodes.BadRequest)
        assert(responseAs[ExceptionResponse] == ExceptionResponse("No events at region=Moscow"))
      }
    }
  }

  describe("GET http://localhost:8080/get/events/topic") {

    it("возвращает все мероприятия с указанной темой") {
      (mockUserService.findEventByTopic _)
        .expects("business")
        .returns(Future.successful(Seq(sampleEvent)))

      Get("/get/events/topic", "business") ~> route ~> check {
        assert(status == StatusCodes.OK)
        assert(responseAs[Seq[Event]] == (Seq(sampleEvent)))
      }
    }

    it("возвращает код 400 и предупреждение, если мероприятий  данной тематикой нет") {
      (mockUserService.findEventByTopic _)
        .expects("not business")
        .returns(Future.failed(NoSuchTopicException("not business")))

      Get("/get/events/topic", "not business") ~> route ~> check {
        assert(status == StatusCodes.BadRequest)
        assert(responseAs[ExceptionResponse] == ExceptionResponse("No events with topic=not business"))
      }
    }
  }

  describe("GET http://localhost:8080/get/recommendations/{userId}") {

    it("возвращает рекомендованные пользователю мероприятия") {
      (mockUserService.showRecommendations _)
        .expects(1)
        .returns(Future.successful(Seq(sampleEvent, sampleEvent2)))

      Get("/get/recommendations/1") ~> route ~> check {
        assert(status == StatusCodes.OK)
        assert(responseAs[Seq[Event]] == (Seq(sampleEvent, sampleEvent2)))
      }
    }

    it("возвращает код 400 и предупреждение, если у пользователя нет интересов") {
      (mockUserService.showRecommendations _)
        .expects(2)
        .returns(Future.failed(NoInterestTopicException()))

      Get("/get/recommendations/2") ~> route ~> check {
        assert(status == StatusCodes.BadRequest)
        assert(responseAs[ExceptionResponse] == ExceptionResponse("You are not interested in any topics"))
      }
    }
  }

  describe("POST http://localhost:8080/add/topic/{userId}") {

    it("возвращает рекомендованные пользователю мероприятия") {
      (mockUserService.addTopic _)
        .expects(1, "education")
        .returns(Future.successful(Some(sampleUser3)))

      Post("/add/topic/1", "education") ~> route ~> check {
        assert(status == StatusCodes.OK)
        assert(responseAs[Option[User]].contains(sampleUser3))
      }
    }
  }


  private val mockUserService = mock[UserService]
  private val route = {
    Route.seal(
      new UserApi(mockUserService).route
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
    id_org = 3,
    name = "Sample_event_name_2",
    topic = "fun",
    date = "2020-01-01",
    time = "19:00",
    price = 100,
    description = "Sample_event_description",
    region = "dnsanda"
  )

  private val sampleUser = tables.User(
    id_user = Some(1),
    name = "Vasya",
    region = "Saint-Petersburg",
    topics = Some("business,fun"),
    email = "email",
    events = None
  )

  private val sampleUser3 = tables.User(
    id_user = Some(1),
    name = "Vasya",
    region = "Saint-Petersburg",
    topics = Some("business,fun,education"),
    email = "email",
    events = None
  )

  private val sampleUser2 = tables.User(
    id_user = Some(2),
    name = "Vasya",
    region = "Saint-Petersburg",
    topics = None,
    email = "email",
    events = None
  )

  private val sampleEventUser = tables.EventUser(1, 1)

  private val expectedInt = 1

}
