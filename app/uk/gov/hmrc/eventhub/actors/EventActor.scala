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

package uk.gov.hmrc.eventhub.actors

import akka.actor._

import javax.inject._
import play.api.Configuration
import uk.gov.hmrc.eventhub.model.{Event, Subscriber, SubscriberWorkItem}
import uk.gov.hmrc.eventhub.service.SubscriberEventService

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration.DurationInt

object EventActor {
  def props = Props[EventActor]

  case class SendEvents(subscribers: List[SubscriberWorkItem], e: Event)
  case object ProcessSubscribers
}

class EventActor @Inject() (subService: SubscriberEventService) extends Actor {
  import EventActor._
  implicit val exec: ExecutionContextExecutor = context.dispatcher
  context.system.scheduler.scheduleWithFixedDelay(1.second, 5.minutes, self, ProcessSubscribers)

  def receive = {
    case SendEvents(s, e) =>
      println(s"sending events $s")
      s.foreach{s =>
        context.actorOf(Props(new SendEvent(subService, s, e)))
      }
    case ProcessSubscribers => println("processing subscribers")
  }
}
