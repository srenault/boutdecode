package actors

import java.util.Date
import play.api._
import play.api.libs.iteratee._
import play.api.libs.concurrent._
import play.api.libs.iteratee.Concurrent._
import play.api.Play.current
import play.api.libs.json._
import play.api.libs.json.Json._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import reactivemongo.core.commands.LastError
import akka.actor._
import akka.actor.Actor._

import models._

class FeedsActor extends Actor {

  import FeedsActor._

  private var streams: Map[ChannelID, Channel[JsObject]] = Map.empty

  def receive = {

    case Listen() => {
      val chanID = FeedsActor.generateChannelID
      lazy val channel: Enumerator[JsObject] = Concurrent.unicast(
        channel => self ! Init(chanID, channel),
        onComplete = self ! Stop(chanID),
        onError = { case(error, _) =>
            Logger.error("[Actor] Error during streaming feeds")
        }
      )
      sender ! channel
    }

    case Init(chanID, channel) => {
      Logger.info("[Actor] Stream to " + chanID)
      streams += (chanID -> channel)
    }

    case IsAlive() => streams.foreach {
      case (chanID, channel) => channel.push(Input.Empty)
    }

    case Stop(chanID) => {
      Logger.warn("[Actor] Stream %s has been closed ...".format(chanID))
      streams = streams.filter {
        case (channelID, _) => chanID != channelID
      }
    }

    case NewFeed(feed: JsObject) => {
      pushToChannel(feed)
    }
  }

  private def pushToChannel(feed: JsObject) = {
    streams.foreach { case (_, channel) =>
      channel.push(feed)
    }
  }
}

object FeedsActor {
  import play.api.Play.current
  import scala.concurrent.duration._

  type ChannelID = Long
  sealed trait Event
  case class Listen() extends Event
  case class Stop(chanID: ChannelID) extends Event
  case class NewFeed(commit: JsObject)
  case class Init(chanID: ChannelID, p: Channel[JsObject])
  case class IsAlive()

  lazy val system = ActorSystem("commitsRoom")
  lazy val ref = Akka.system.actorOf(Props[FeedsActor])

  def generateChannelID = new Date().getTime

  def start = Akka.system.scheduler.schedule(1 second, 1 second) {
    FeedsActor.ref ! IsAlive()
  }
}
