package models

import reactivemongo.bson.BSONObjectID
import play.api.libs.json._
import play.api.libs.json.Json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._


case class Commit(author: String, date: String, message: String, diff: String)

object Commit extends Function4[String, String, String, String, Commit] {

  object json {


	implicit val readCommit = Json.reads[Commit]

	val writeObjectId: Reads[JsObject] = {
    	def id = BSONObjectID.generate.stringify
		(__).json.pickBranch and
		(__ \ '_id).json.put(
	        Json.obj(
	          "$oid" -> JsString(id)
	        )
      	) reduce
  	}

  }
}
