package storage

import tables.Event

import scala.concurrent.Future

/**
 * Хранилище событий - работа с таблицей EventTable
 */

trait EventStorage {

  def addEvent(orgId: Long, name: String, topic: String, date: String, time: String, price: Int, description: String, region: String): Future[Int]

  def getEventByName(name: String): Future[Option[Event]]

  def getEventById(eventId: Long): Future[Option[Event]]

  def getEventIdByEventName(name: String): Future[Option[Long]]

  def updateNumberVisitors(eventId: Long, new_visitors: Int): Future[Int]

  def getAllEventsByOrgId(orgId: Long): Future[Seq[Event]]

  def getEventByTopic(topic: String): Future[Seq[Event]]

  def getEventByRegion(region: String): Future[Seq[Event]]

  def getEventByDate(date: String): Future[Seq[Event]]


}
