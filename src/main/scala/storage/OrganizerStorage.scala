package storage

import tables.{Event, Organizer}

import scala.concurrent.Future

/**
 * Хранилище организаторов - работа с таблицей OrganizerTable
 */

trait OrganizerStorage {

  def addOrganizer(name: String): Future[Int]

  def getOrganizerByName(name: String): Future[Option[Organizer]]

  def countEventsOfOrganizer(orgId: Long): Future[Int]

  def updateNumberEvents(orgId: Long, new_events: Int): Future[Int]

  def getOrganizerById(orgId: Long): Future[Option[Organizer]]



}
