package actors

import akka.actor.{Actor, ActorRef, Props}
import models.Table
import message._

class TablesActor extends Actor {

  protected val tables = scala.collection.mutable.Map(
    1 -> Table(Some(1), "table - James Bond", 7),
    2 -> Table(Some(2), "table - Mission Impossible", 4)
  )

  protected val subscribers = scala.collection.mutable.Set[ActorRef]()

  override def receive = {
    case SubscribeTables => subscribe(sender())
    case UnsubscribeTables => unsubscribe(sender())
    case AddTable(table) => addTable(table)
    case UpdateTable(table) => updateTable(table)
    case RemoveTable(id) => removeTable(id)
    case any => unhandled(any)
  }

  private def subscribe(subscriber: ActorRef): Unit = {
    subscribers.add(sender)
    sender ! TableList(tables.values.toList)
  }

  private def unsubscribe(subscriber: ActorRef): Unit = {
    subscribers.remove(sender)
  }

  private def addTable(table: Table): Unit = {
    val newTable =  table.withId(tables.size + 1)
    tables.put(newTable.id.get, newTable)
    sender ! TableAdded(table)
    subscribers foreach { subscriber =>
      subscriber ! TableAdded(table)
    }
  }

  private def updateTable(table: Table): Unit = {
    table.id.map(id => tables.get(id)) match {
      case Some(_) =>
        tables.put(table.id.get, table)
        sender ! TableUpdated(table)
        subscribers foreach { subscriber =>
          subscriber ! TableUpdated(table)
        }
      case None => sender ! TableUpdateFailed(table.id.get)
    }
  }

  private def removeTable(id: Int): Unit = {
    tables.remove(id) match {
      case Some(_) =>
        sender ! TableRemoved(id)
        subscribers foreach { subscriber =>
          subscriber ! TableRemoved(id)
        }
      case None => sender ! TableRemoveFailed(id)
    }
  }
}

object TablesActor {
  def props() = Props(new TablesActor())
}