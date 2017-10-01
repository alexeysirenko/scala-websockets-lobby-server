package actors

import akka.actor.{Actor, Props}
import message.{Login, LoginFailed, LoginSuccessful}
import models.User

/**
  * Handles authorization
  */
class AuthorizationActor extends Actor {

  /**
    * List of existing users.
    */
  protected val users = List(
    User("user1234", "password1234", "admin"),
    User("user5678", "password5678", "user")
  )

  override def receive = {
    case message: Login =>
      users.find(u => u.username == message.username && u.password == message.password) match {
        case Some(user) => sender ! LoginSuccessful(user.role)
        case None => sender ! LoginFailed("Login and password does not match")
      }
    case message => unhandled(message)
  }
}

object AuthorizationActor {
  def props() = Props(new AuthorizationActor())
}
