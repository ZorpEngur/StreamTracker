package com.streamTracker.notification

import com.github.philippheuer.events4j.core.EventManager
import com.github.twitch4j.TwitchClient
import com.github.twitch4j.TwitchClientHelper
import com.github.twitch4j.chat.TwitchChat
import com.streamTracker.ApplicationProperties
import com.streamTracker.database.model.DatabaseUserModel
import com.streamTracker.database.twitch.TwitchBotService
import spock.lang.Specification

class TwitchLiveBotSpec extends Specification {

    void "Should load users"() {
        given:
        TwitchBotService service = Mock() {
            1 * getStreamerModels() >> [new StreamModel("stream", [new DatabaseUserModel(1L, "user")]),
                                        new StreamModel("existingStream", [new DatabaseUserModel(1L, "user")])]
        }
        TwitchChat chat = Mock() {
            getChannels() >> ["existingStream"]
        }
        TwitchClient client = Mock() {
            getChat() >> chat
        }
        def bot = new TwitchLiveBot(service, Mock(DiscordBot), Mock(ApplicationProperties), Mock(StreamRecorder))
        bot.twitchClient = client

        when:
        bot.loadUsers()

        then:
        bot.streamModels.size() == 2
        1 * chat.joinChannel(_)
        1 * client.getClientHelper() >> Mock(TwitchClientHelper)
        4 * client.getEventManager() >> Mock(EventManager)
    }
}
