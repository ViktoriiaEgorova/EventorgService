package tables

import org.scalatest.matchers.should.Matchers
import slick.jdbc.H2Profile.api._

class EventUserQueryRepositoryTest extends EventorgDatabaseSuite with Matchers {

  import tables.EventUserQueryRepository._

  protected val SampleEventUsersCheck = Seq(
    EventUser(1, 1),
    EventUser(1, 2)
  )
  protected val SampleEventUsersCheck2 = Seq(
    EventUser(1, 2)
  )


  test("listEventUsersByEventId should return eventuser") {
    //println(addEventUserFut(1, 2))
    for {
      _ <- addEventUser(1, 1)
      _ <- addEventUser(1, 2)
      eventuser <- listEventUsersByEventId(1)
    } yield eventuser should contain theSameElementsAs SampleEventUsersCheck
  }

  test("listEventUsersByUserId should return eventuser") {
    //addEventUser(1, 2)
    for {
      _ <- addEventUser(1, 1)
      _ <- addEventUser(1, 2)
      eventuser <- listEventUsersByUserId(2)
    } yield eventuser should contain theSameElementsAs SampleEventUsersCheck2
  }

}
