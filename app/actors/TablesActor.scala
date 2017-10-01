package actors

import akka.actor.{Actor, ActorRef, Props}
import msg.{SubscribeTables, TableList, UnsubscribeTables}

class TablesActor extends Actor {

  protected val tables = List(
    TableList.Table(1, "table - James Bond", 7),
    TableList.Table(2, "table - Mission Impossible", 4)
  )

  protected val subscribers = scala.collection.mutable.Set[ActorRef]()

  override def receive = {
    case SubscribeTables =>
      subscribers.add(sender)
      sender ! TableList(tables)
    case UnsubscribeTables =>
      subscribers.remove(sender)
    case _ => unhandled()
  }
}

object TablesActor {
  def props() = Props(new TablesActor())
}