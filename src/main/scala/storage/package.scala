import java.util.UUID

import slick.jdbc.H2Profile.api._
import slick.jdbc.JdbcBackend.Database
import tables.EventQueryRepository.AllEvents
import tables.EventUserQueryRepository.AllEventUsers
import tables.OrganizerQueryRepository.AllOrganizers
import tables.UserQueryRepository.AllUsers
import tables.{EventQueryRepository, EventUserQueryRepository, OrganizerQueryRepository, UserQueryRepository}

package object storage {

  val db = Database.forURL(
    s"jdbc:h2:mem:${UUID.randomUUID()}",
    driver = "org.h2.Driver",
    keepAliveConnection = true
  )

  private val initSchema =
    (EventQueryRepository.AllEvents.schema ++ OrganizerQueryRepository.AllOrganizers.schema ++ UserQueryRepository.AllUsers.schema ++ EventUserQueryRepository.AllEventUsers.schema).create



  db.run(initSchema)

}
