package controllers


import scala.concurrent.future
import scala.concurrent.duration._
import akka.util.Timeout
import akka.pattern.ask
import reactivemongo.api._
import play.api._
import play.api.mvc._
import play.api.libs.{ Comet, EventSource }
import play.api.libs.functional.syntax._
import play.api.libs.iteratee._
import play.api.libs.json._
import play.api.libs.json.Json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import dao._
import models._
import actors.FeedsActor
import actors.FeedsActor._

object Application extends Controller {

  def index = Action { implicit request =>
    Ok(views.html.index())
  }

  def mobile = Action { implicit request =>
    Ok(views.html.mobile())
  }

  def createCommit = Action(parse.json) { request =>
    Async {
      request.body.validate(
        Commit.json.readFromWeb andKeep Commit.json.writeObjectId
      ) map { json =>
        CommitDAO.create(json).map {
          case Right(id) => {
            FeedsActor.ref ! NewFeed(json)
            Ok(Json.obj("id" -> id.stringify))
          }
          case Left(error) => BadRequest
        }
      } recoverTotal {
        case e: JsError => future(BadRequest(
          Json.obj("errors" -> JsError.toFlatJson(e))
        ))
      }
    }
  }

  def feeds = Action { implicit request =>
    implicit val timeout = Timeout(10 seconds)
    Async {
      (FeedsActor.ref ? Listen()).mapTo[Enumerator[JsValue]].map { commit =>
        implicit val feed = Comet.CometMessage[JsValue](_.toString)
        Ok.stream(commit &> EventSource())
          .withHeaders(CONTENT_TYPE -> "text/event-stream")
      } recover {
        case e: Exception => InternalServerError(e.getMessage)
      }
    }
  }
}
