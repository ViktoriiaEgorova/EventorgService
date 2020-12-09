package tables

import org.scalatest.matchers.should.Matchers
import slick.jdbc.H2Profile.api._

class EventQueryRepositoryTest extends EventorgDatabaseSuite with Matchers{

  import tables.EventQueryRepository._

  protected val SampleEventsCheck = Seq(
    Event(Some(1), 1, "First event", "First topic", "2020-03-16", "19:00", 100, "First description", "First region"),
    Event(Some(2), 2, "Second event", "Second topic", "2020-03-17", "18:00", 150, "Second description", "Second region")
  )


  test("events should query all events") {
    for {
      events <- AllEvents.result
    } yield events should contain theSameElementsAs SampleEventsCheck
  }

  test("findEvent should return event") {
    for {
      event <- findEvent(1)
    } yield assert(event.contains(SampleEventsCheck.head))
  }


}