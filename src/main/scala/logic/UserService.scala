package logic

import tables.{Event, EventUser, User}

import scala.concurrent.Future

trait UserService {

  /**
   * Регистрация пользователя
   *
   * @param name название пользователя
   * @param email почтовый адрес пользователя
   * @param region регион пользователя
   * @return созданный пользователь
   */
  def registerUserByName(name: String, email: String, region: String): Future[Option[User]]

  /**
   * Регистрация пользователя на мероприятие
   *
   * @param userId
   * @param eventId
   * @return обновленное число зарегистрировавшихся
   */
  def registerForEvent(userId: Long, eventId: Long): Future[Int]

  /**
   * Информация о пользователе
   *
   * @param userId id пользователя
   * @return пользователь
   */
  def findUserById(userId: Long): Future[Option[User]]

  /**
   * Информация о мероприятии
   *
   * @param eventId id организатора
   * @return мероприятие
   */
  def findEventById(eventId: Long): Future[Option[Event]]

  /**
   * Показывает все мероприятия, на которые зарегистрировался пользователь
   *
   * @param userId id пользователя
   * @return список мероприятий
   */
  def showAllRegisteredEvents(userId: Long):  Future[Seq[Option[Event]]]

  /**
   * Поиск мероприятия по теме
   *
   * @param topic тема
   * @return список мероприятий
   */
  def findEventByTopic(topic: String):  Future[Seq[Event]]

  /**
   * Поиск мероприятия по региону
   *
   * @param region регион
   * @return список мероприятий
   */
  def findEventByRegion(region: String):  Future[Seq[Event]]

  /**
   * Поиск мероприятия по дате
   *
   * @param date дата
   * @return список мероприятий
   */
  def findEventByDate(date: String):  Future[Seq[Event]]

  /**
   * Показывает все мероприятия, которые могли бы быть интересны пользователю
   *
   * @param userId интересные темы
   * @return список мероприятий
   */
  def showRecommendations(userId: Long):  Future[Seq[Event]]

  /**
   * Добавляет тему интереса
   *
   * @param topic новая тема
   * @param userId
   * @return обновленный пользователь
   */
  def addTopic(userId: Long, topic: String):  Future[Option[User]]



}
