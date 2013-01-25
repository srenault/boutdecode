package dao

import scala.concurrent.future
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.Play.current
import play.api.libs.json._
import play.modules.reactivemongo._
import play.modules.reactivemongo.PlayBsonImplicits._
import reactivemongo.api.SortOrder.{ Ascending, Descending }
import reactivemongo.core.commands.LastError
import reactivemongo.bson.BSONObjectID


object CommitDAO {

  val collection = ReactiveMongoPlugin.collection("commits")

  def handleLastError(lastError: LastError, success: Option[BSONObjectID], error: => String): Either[String, BSONObjectID] = {
    if(lastError.ok) {
      success map(Right(_)) getOrElse Left(error + " : ObjectId is none")
    } else Left(error)
  }

  def create(commit: JsValue): Future[Either[String, BSONObjectID]] = {
    collection.insert(commit).map { lastError =>
      val id = (commit \ "_id" \ "$oid").asOpt[String].map(BSONObjectID(_))
      handleLastError(lastError, id, "An error occurred while creating a commit")
    }
  }
}
