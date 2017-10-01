package actors

import akka.actor.{Actor, ActorRef, Props}
import models.User
import msg.{Login, LoginFailed, LoginSuccessful}

class AuthorizationActor extends Actor {

  protected val users = List(
    User("user1234", "password1234", "user"),
    User("administrator", "password42", "admin")
  )

  override def receive = {
    case message: Login =>
      users.find(u => u.username == message.username && u.password == message.password) match {
        case Some(user) => sender ! LoginSuccessful(user.role)
        case None => sender ! LoginFailed("Login and password does not match")
      }
    case _ => unhandled()
  }
}

object AuthorizationActor {
  def props() = Props(new AuthorizationActor())
}
