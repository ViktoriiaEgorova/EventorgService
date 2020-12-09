package logic

import scala.concurrent.Future
import tables.{Event, Organizer}

trait OrganizerService {

  /**
   * Регистрация организатора
   *
   * @param name название организатора
   * @return созданный организатор
   */
  def registerOrganizerByName(name: String): Future[Option[Organizer]]

  /**
   * Создание мероприятия
   *
   * @param orgId
   * @param name название мероприятия
   * @param topic
   * @param date
   * @param time
   * @param price
   * @param description
   * @param region
   * @return созданное мероприятие
   */
  def createEvent(orgId: Long, name: String, topic: String, date: String, time: String, price: Int, description: String, region: String): Future[Option[Event]]

  /**
   * Информация об организаторе
   *
   * @param orgId id организатора
   * @return организатор
   */
  def findOrganizerById(orgId: Long): Future[Option[Organizer]]

  /**
   * Информация о мероприятии
   *
   * @param eventId id организатора
   * @return мероприятие
   */
  def findEventById(eventId: Long): Future[Option[Event]]

  /**
   * Показывает все созданные мероприятия
   *
   * @param orgId id организатора
   * @return список мероприятий
   */
  def showAllCreatedEvents(orgId: Long): Future[Seq[Event]]



}