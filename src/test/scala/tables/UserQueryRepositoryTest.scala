package tables
import org.scalatest.matchers.should.Matchers
import slick.jdbc.H2Profile.api._

class UserQueryRepositoryTest extends EventorgDatabaseSuite with Matchers {

  import tables.UserQueryRepository._

  protected val SampleUsersCheck = Seq(
    User(Some(1), "First user", "First region", Some("First topic"), "First email")
  )

  test("organizers should query all organizers") {
    for {
      organizer <- AllUsers.result
    } yield organizer should contain theSameElementsAs SampleUsersCheck
  }

  test("findEvent should return event") {
    for {
      event <- findUser(1)
    } yield assert(event.contains(SampleUsersCheck.head))
  }

}