package com.danielasfregola.twitter4s.http.clients.streaming

import akka.http.scaladsl.model.{HttpEntity, _}
import akka.stream.scaladsl.Source
import com.danielasfregola.twitter4s.entities.streaming._
import com.danielasfregola.twitter4s.entities.streaming.common._
import com.danielasfregola.twitter4s.entities.streaming.site._
import com.danielasfregola.twitter4s.entities.streaming.user._
import com.danielasfregola.twitter4s.entities.{DirectMessage, Tweet}
import com.danielasfregola.twitter4s.util.streaming.ClientSpec

class StreamingClientSpec extends ClientSpec {

  abstract class StreamingClientSpecContext extends ClientSpecContext with StreamingClient {

    implicit val exampleRequest: HttpRequest = Get("an-example-request", ContentTypes.`application/json`)

    def buildResponse(resourcePath: String): HttpResponse = {
      val contentType = ContentTypes.`application/json`
      val streamInput = getClass.getResourceAsStream(resourcePath)
      val data = scala.io.Source.fromInputStream(streamInput).getLines.filter(_.nonEmpty)
      val chunks = data.map(d => HttpEntity.ChunkStreamPart(s"$d\r\n")).toList
      HttpResponse(entity = HttpEntity.Chunked(contentType, Source(chunks)))
    }

    def redirectMessages: PartialFunction[StreamingMessage, Unit] = {
      case msg => transport.ref ! StreamingUpdate(msg)
    }

    def readStreamUpdatesAs[T <: StreamingMessage: Manifest](path: String): Seq[StreamingUpdate] =
      loadJsonAs[Seq[T]](path).map(StreamingUpdate)
  }

  "StreamingClient" should {

    "parse a stream of data and output the corresponding entities of type" in {

      "Tweet" in new StreamingClientSpecContext {
        val response = buildResponse("/twitter/streaming/common/tweets.json")
        processBody(response, killSwitch)(redirectMessages)

        val messages = readStreamUpdatesAs[Tweet]("/fixtures/streaming/common/tweets.json")
        transport.expectMsgAllOf(messages: _*)
      }

      "DirectMessage" in new StreamingClientSpecContext {
        val response = buildResponse("/twitter/streaming/user/direct_messages.json")
        processBody(response, killSwitch)(redirectMessages)

        val messages = readStreamUpdatesAs[DirectMessage]("/fixtures/streaming/user/direct_messages.json")
        transport.expectMsgAllOf(messages: _*)
      }

      "ControlMessage" in new StreamingClientSpecContext {
        val response = buildResponse("/twitter/streaming/site/control_messages.json")
        processBody(response, killSwitch)(redirectMessages)

        val messages = readStreamUpdatesAs[ControlMessage]("/fixtures/streaming/site/control_messages.json")
        transport.expectMsgAllOf(messages: _*)
      }

      "DisconnectMessage" in new StreamingClientSpecContext {
        val response = buildResponse("/twitter/streaming/common/disconnect_messages.json")
        processBody(response, killSwitch)(redirectMessages)

        val messages = readStreamUpdatesAs[DisconnectMessage]("/fixtures/streaming/common/disconnect_messages.json")
        transport.expectMsgAllOf(messages: _*)
      }

      "TweetEvent" in new StreamingClientSpecContext {
        val response = buildResponse("/twitter/streaming/user/events.json")
        processBody(response, killSwitch)(redirectMessages)

        val messages = readStreamUpdatesAs[TweetEvent]("/fixtures/streaming/user/events.json")
        transport.expectMsgAllOf(messages: _*)
      }

      "FriendsLists" in new StreamingClientSpecContext {
        val response = buildResponse("/twitter/streaming/user/friends_lists.json")
        processBody(response, killSwitch)(redirectMessages)

        val messages = readStreamUpdatesAs[FriendsLists]("/fixtures/streaming/user/friends_lists.json")
        transport.expectMsgAllOf(messages: _*)
      }

      "FriendsListsStringified" in new StreamingClientSpecContext {
        val response = buildResponse("/twitter/streaming/user/friends_lists_stringified.json")
        processBody(response, killSwitch)(redirectMessages)

        val messages = readStreamUpdatesAs[FriendsListsStringified]("/fixtures/streaming/user/friends_lists_stringified.json")
        transport.expectMsgAllOf(messages: _*)
      }

      "LimitNotice" in new StreamingClientSpecContext {
        val response = buildResponse("/twitter/streaming/common/limit_notices.json")
        processBody(response, killSwitch)(redirectMessages)

        val messages = readStreamUpdatesAs[LimitNotice]("/fixtures/streaming/common/limit_notices.json")
        transport.expectMsgAllOf(messages: _*)
      }

      "LocationDeletionNotice" in new StreamingClientSpecContext {
        val response = buildResponse("/twitter/streaming/common/location_deletion_notices.json")
        processBody(response, killSwitch)(redirectMessages)

        val messages = readStreamUpdatesAs[LocationDeletionNotice]("/fixtures/streaming/common/location_deletion_notices.json")
        transport.expectMsgAllOf(messages: _*)
      }

      "StatusDeletionNotice" in new StreamingClientSpecContext {
        val response = buildResponse("/twitter/streaming/common/status_deletion_notices.json")
        processBody(response, killSwitch)(redirectMessages)

        val messages = readStreamUpdatesAs[StatusDeletionNotice]("/fixtures/streaming/common/status_deletion_notices.json")
        transport.expectMsgAllOf(messages: _*)
      }

      "StatusWithheldNotice" in new StreamingClientSpecContext {
        val response = buildResponse("/twitter/streaming/common/status_withheld_notices.json")
        processBody(response, killSwitch)(redirectMessages)

        val messages = readStreamUpdatesAs[StatusWithheldNotice]("/fixtures/streaming/common/status_withheld_notices.json")
        transport.expectMsgAllOf(messages: _*)
      }

      "UserWithheldNotice" in new StreamingClientSpecContext {
        val response = buildResponse("/twitter/streaming/common/user_withheld_notices.json")
        processBody(response, killSwitch)(redirectMessages)

        val messages = readStreamUpdatesAs[UserWithheldNotice]("/fixtures/streaming/common/user_withheld_notices.json")
        transport.expectMsgAllOf(messages: _*)
      }

      "UserEnvelopTweet" in new StreamingClientSpecContext {
        val response = buildResponse("/twitter/streaming/site/user_envelop_tweet.json")
        processBody(response, killSwitch)(redirectMessages)

        val messages = readStreamUpdatesAs[UserEnvelopTweet]("/fixtures/streaming/site/user_envelop_tweet.json")
        transport.expectMsgAllOf(messages: _*)
      }

      "UserEnvelopFriendsLists" in new StreamingClientSpecContext {
        val response = buildResponse("/twitter/streaming/site/user_envelops_friends_list.json")
        processBody(response, killSwitch)(redirectMessages)

        val messages = readStreamUpdatesAs[UserEnvelopFriendsLists]("/fixtures/streaming/site/user_envelops_friends_list.json")
        transport.expectMsgAllOf(messages: _*)
      }

      "UserEnvelopFriendsListsStringified" in new StreamingClientSpecContext {
        val response = buildResponse("/twitter/streaming/site/user_envelops_friends_list_stringified.json")
        processBody(response, killSwitch)(redirectMessages)

        val messages = readStreamUpdatesAs[UserEnvelopFriendsListsStringified]("/fixtures/streaming/site/user_envelops_friends_list_stringified.json")
        transport.expectMsgAllOf(messages: _*)
      }

      "UserEnvelopDirectMessage" in new StreamingClientSpecContext {
        val response = buildResponse("/twitter/streaming/site/user_envelop_direct_message.json")
        processBody(response, killSwitch)(redirectMessages)

        val messages = readStreamUpdatesAs[UserEnvelopDirectMessage]("/fixtures/streaming/site/user_envelop_direct_message.json")
        transport.expectMsgAllOf(messages: _*)
      }

      "UserEnvelopTwitterListEvent" in new StreamingClientSpecContext {
        val response = buildResponse("/twitter/streaming/site/user_envelop_twitter_list_event.json")
        processBody(response, killSwitch)(redirectMessages)

        val messages = readStreamUpdatesAs[UserEnvelopTwitterListEvent]("/fixtures/streaming/site/user_envelop_twitter_list_event.json")
        transport.expectMsgAllOf(messages: _*)
      }

      "UserEnvelopTweetEvent" in new StreamingClientSpecContext {
        val response = buildResponse("/twitter/streaming/site/user_envelop_tweet_event.json")
        processBody(response, killSwitch)(redirectMessages)

        val messages = readStreamUpdatesAs[UserEnvelopTweetEvent]("/fixtures/streaming/site/user_envelop_tweet_event.json")
        transport.expectMsgAllOf(messages: _*)
      }

      "UserEnvelopSimpleEvent" in new StreamingClientSpecContext {
        val response = buildResponse("/twitter/streaming/site/user_envelop_simple_event.json")
        processBody(response, killSwitch)(redirectMessages)

        val messages = readStreamUpdatesAs[UserEnvelopSimpleEvent]("/fixtures/streaming/site/user_envelop_simple_event.json")
        transport.expectMsgAllOf(messages: _*)
      }

      "UserEnvelopWarningMessage" in new StreamingClientSpecContext {
        val response = buildResponse("/twitter/streaming/site/user_envelop_warning_message.json")
        processBody(response, killSwitch)(redirectMessages)

        val messages = readStreamUpdatesAs[UserEnvelopWarningMessage]("/fixtures/streaming/site/user_envelop_warning_message.json")
        transport.expectMsgAllOf(messages: _*)
      }

      "UserEnvelopTweetStringified" in new StreamingClientSpecContext {
        val response = buildResponse("/twitter/streaming/site/user_envelop_tweet_stringified.json")
        processBody(response, killSwitch)(redirectMessages)

        val messages = readStreamUpdatesAs[UserEnvelopTweetStringified]("/fixtures/streaming/site/user_envelop_tweet_stringified.json")
        transport.expectMsgAllOf(messages: _*)
      }

      "UserEnvelopDirectMessageStringified" in new StreamingClientSpecContext {
        val response = buildResponse("/twitter/streaming/site/user_envelop_direct_message_stringified.json")
        processBody(response, killSwitch)(redirectMessages)

        val messages = readStreamUpdatesAs[UserEnvelopDirectMessageStringified]("/fixtures/streaming/site/user_envelop_direct_message_stringified.json")
        transport.expectMsgAllOf(messages: _*)
      }

      "UserEnvelopTwitterListEventStringified" in new StreamingClientSpecContext {
        val response = buildResponse("/twitter/streaming/site/user_envelop_twitter_list_event_stringified.json")
        processBody(response, killSwitch)(redirectMessages)

        val messages = readStreamUpdatesAs[UserEnvelopTwitterListEventStringified]("/fixtures/streaming/site/user_envelop_twitter_list_event_stringified.json")
        transport.expectMsgAllOf(messages: _*)
      }

      "UserEnvelopTweetEventStringified" in new StreamingClientSpecContext {
        val response = buildResponse("/twitter/streaming/site/user_envelop_tweet_event_stringified.json")
        processBody(response, killSwitch)(redirectMessages)

        val messages = readStreamUpdatesAs[UserEnvelopTweetEventStringified]("/fixtures/streaming/site/user_envelop_tweet_event_stringified.json")
        transport.expectMsgAllOf(messages: _*)
      }

      "UserEnvelopSimpleEventStringified" in new StreamingClientSpecContext {
        val response = buildResponse("/twitter/streaming/site/user_envelop_simple_event_stringified.json")
        processBody(response, killSwitch)(redirectMessages)

        val messages = readStreamUpdatesAs[UserEnvelopSimpleEventStringified]("/fixtures/streaming/site/user_envelop_simple_event_stringified.json")
        transport.expectMsgAllOf(messages: _*)
      }

      "UserEnvelopWarningMessageStringified" in new StreamingClientSpecContext {
        val response = buildResponse("/twitter/streaming/site/user_envelop_warning_message_stringified.json")
        processBody(response, killSwitch)(redirectMessages)

        val messages = readStreamUpdatesAs[UserEnvelopWarningMessageStringified]("/fixtures/streaming/site/user_envelop_warning_message_stringified.json")
        transport.expectMsgAllOf(messages: _*)
      }

      "WarningMessage" in new StreamingClientSpecContext {
        val response = buildResponse("/twitter/streaming/common/warning_messages.json")
        processBody(response, killSwitch)(redirectMessages)

        val messages = readStreamUpdatesAs[WarningMessage]("/fixtures/streaming/common/warning_messages.json")
        transport.expectMsgAllOf(messages: _*)
      }
    }
  }
}
