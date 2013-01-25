package models

import reactivemongo.bson.BSONObjectID
import play.api.libs.json._
import play.api.libs.json.Json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._


case class Commit()

object Commit {

  object json {
   val readFromWeb =
	(
		(__ \ 'hash).read[String] and
		(__ \ 'author).read[String] and
		(__ \ 'date).read[String] and
		(__ \ 'message).read[String] and
		(__ \ 'diff).read[String]
	) tupled

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
