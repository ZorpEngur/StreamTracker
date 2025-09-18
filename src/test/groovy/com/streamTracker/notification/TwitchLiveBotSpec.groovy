package com.streamTracker.notification

import com.github.philippheuer.events4j.core.EventManager
import com.github.twitch4j.TwitchClient
import com.github.twitch4j.TwitchClientHelper
import com.github.twitch4j.chat.TwitchChat
import com.streamTracker.ApplicationProperties
import com.streamTracker.database.model.NotificationPlatform
import com.streamTracker.database.twitch.TwitchBotService
import com.streamTracker.recorder.ChatRecorder
import com.streamTracker.recorder.StreamRecorder
import spock.lang.Specification

class TwitchLiveBotSpec extends Specification {

    void "Should load users"() {
        given:
        TwitchBotService service = Mock() {
            1 * getStreamerModels() >> [new StreamModel("stream", true, [new StreamModel.UserModel(1, true, NotificationPlatform.DISCORD)]),
                                        new StreamModel("existingStream", true, [new StreamModel.UserModel(1, false, NotificationPlatform.DISCORD)])]
        }
        TwitchChat chat = Mock() {
            getChannels() >> ["existingStream"]
        }
        TwitchClient client = Mock() {
            getChat() >> chat
        }
        def bot = new TwitchLiveBot(service, Mock(DiscordBot), Mock(ApplicationProperties), Mock(StreamRecorder), Mock(ChatRecorder))
        bot.twitchClient = client

        when:
        bot.registerEvents()
        bot.loadUsers()

        then:
        bot.streamModels.size() == 2
        1 * chat.joinChannel(_)
        1 * client.getClientHelper() >> Mock(TwitchClientHelper)
        6 * client.getEventManager() >> Mock(EventManager)
    }
}
