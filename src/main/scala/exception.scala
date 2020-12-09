
sealed abstract class EventorgException(message: String) extends Exception(message)

final case class EventNotFoundException(name: String) extends EventorgException(s"Event with name=$name not found")

final case class UserRegistrationException() extends EventorgException(s"Some problems with registration")

final case class UserRegistrationEmailException(email: String) extends EventorgException(s"User with email=$email already exists")
