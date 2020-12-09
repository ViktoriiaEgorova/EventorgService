package tables

import org.scalatest.matchers.should.Matchers
import slick.jdbc.H2Profile.api._

class OrganizerQueryRepositoryTest extends EventorgDatabaseSuite with Matchers {

  import tables.OrganizerQueryRepository._

  protected val SampleOrganizersCheck = Seq(
    Organizer(Some(1), "First organizer")
  )

  test("organizers should query all organizers") {
    for {
      organizer <- AllOrganizers.result
    } yield organizer should contain theSameElementsAs SampleOrganizersCheck
  }

  test("findEvent should return event") {
    for {
      event <- findOrganizer(1)
    } yield assert(event.contains(SampleOrganizersCheck.head))
  }

}
