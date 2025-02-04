/*
 * Copyright 2021 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.eventhub.repository

import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.{FindObservable, Observer, SingleObservable}
import org.mongodb.scala.result.InsertOneResult
import uk.gov.hmrc.eventhub.model.{Event, MongoEvent}
import uk.gov.hmrc.mongo.MongoComponent

import scala.concurrent.{ExecutionContext, Future}
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository

import java.util.UUID
import javax.inject.Inject


class EventHubRepository @Inject()(mongo: MongoComponent)(implicit ec: ExecutionContext) extends PlayMongoRepository[MongoEvent](
  mongoComponent = mongo,
  collectionName = "event-hub",
  domainFormat   = MongoEvent.fmt,
  indexes        = Seq()
){


  def saveEvent(event: Event): Future[InsertOneResult] = collection.insertOne(MongoEvent(event)).toFuture()

  def findEventByMessageId(messageId: UUID): Future[MongoEvent] =
    collection.find(equal("event.messageId", messageId.toString)).first().toFuture()
}


