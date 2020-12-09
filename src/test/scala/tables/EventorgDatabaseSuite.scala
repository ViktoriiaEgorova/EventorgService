package tables

import java.util.UUID

import org.scalactic.source
import org.scalatest.compatible
import org.scalatest.funsuite.AsyncFunSuite
import slick.dbio.{DBIOAction, Effect, NoStream}
import slick.jdbc.H2Profile.api._
import slick.jdbc.JdbcBackend.Database
import tables.EventQueryRepository.AllEvents
import tables.OrganizerQueryRepository.AllOrganizers
import tables.UserQueryRepository.AllUsers
import tables.EventUserQueryRepository.AllEventUsers

abstract class EventorgDatabaseSuite extends AsyncFunSuite {
  protected def test[R, S <: NoStream, E <: Effect](testName: String)
                                                   (testFun: DBIOAction[compatible.Assertion, S, E])
                                                   (implicit pos: source.Position): Unit = {
    super.test(testName) {

      val db = Database.forURL(
        s"jdbc:h2:mem:${UUID.randomUUID()}",
        driver = "org.h2.Driver",
        keepAliveConnection = true
      )

      db.run(
        initSchema
          .andThen(AllEvents ++= SampleEvents)
          .andThen(AllOrganizers ++= SampleOrganizers)
          .andThen(AllUsers ++= SampleUsers)
          .andThen(AllEventUsers ++= SampleEventUsers)
      )
        .flatMap(_ => db.run(testFun))
        .andThen { case _ => db.close() }
    }
  }

  private val initSchema =
    (EventQueryRepository.AllEvents.schema ++ OrganizerQueryRepository.AllOrganizers.schema ++ UserQueryRepository.AllUsers.schema ++ EventUserQueryRepository.AllEventUsers.schema).create


  protected val SampleEvents = Seq(
    Event(None, 1, "First event", "First topic", "2020-03-16", "19:00", 100, "First description", "First region"),
    Event(None, 2, "Second event", "Second topic", "2020-03-17", "18:00", 150, "Second description", "Second region")
  )

  protected val SampleOrganizers = Seq(
    Organizer(None, "First organizer")
  )

  protected val SampleUsers = Seq(
    User(None, "First user", "First region", Some("First topic"), "First email")
  )

  protected val SampleEventUsers = Seq()

}
