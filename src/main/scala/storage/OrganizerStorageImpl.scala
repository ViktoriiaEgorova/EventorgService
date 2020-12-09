package storage
import tables.{Event, EventQueryRepository, Organizer, OrganizerQueryRepository}

import scala.concurrent.Future

class OrganizerStorageImpl extends OrganizerStorage {

  override def addOrganizer(name: String): Future[Int] = {
    db.run(OrganizerQueryRepository.addOrganizer(Organizer(None, name)))
  }

  override def getOrganizerByName(name: String): Future[Option[Organizer]] = {
    db.run(OrganizerQueryRepository.findOrganizerByName(name))
  }

  override def countEventsOfOrganizer(orgId: Long): Future[Int] = {
    db.run(EventQueryRepository.countEventsOfOrganizer(orgId))
  }

  override def updateNumberEvents(orgId: Long, new_events: Int): Future[Int] = {
    db.run(OrganizerQueryRepository.updateNumberEvents(orgId, new_events))
  }

  override def getOrganizerById(orgId: Long): Future[Option[Organizer]] = {
    db.run(OrganizerQueryRepository.findOrganizer(orgId))
  }


}


