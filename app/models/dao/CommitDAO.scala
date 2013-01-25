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

object CommitDAO {

  val collection = ReactiveMongoPlugin.collection("commits")

  def create(commit: JsValue): Future[LastError] = {
    future(LastError(true, None, None, None,None))
  }
}
