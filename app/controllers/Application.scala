package controllers

import scala.concurrent.future
import play.api._
import play.api.mvc._
import reactivemongo.api._
import dao._
import models._
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.libs.json.Json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
import play.api.libs.concurrent.Execution.Implicits.defaultContext


object Application extends Controller {

	def createCommit = Action(parse.json) { request =>
  		Async {
      		request.body.validate(
      			Commit.json.readFromWeb andKeep Commit.json.writeObjectId
      		) map { json =>
      			CommitDAO.create(json).map {
      				case Right(id) => Ok(Json.obj("id" -> id.stringify))
          			case Left(error) => BadRequest
      			}
      		} recoverTotal {
            case e: JsError => future(BadRequest(
              Json.obj("errors" -> JsError.toFlatJson(e))
            ))
          }
  		}
  	}	
}
