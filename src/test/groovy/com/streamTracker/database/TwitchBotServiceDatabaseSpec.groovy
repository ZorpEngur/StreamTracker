package com.streamTracker.database

import com.streamTracker.DatabaseSpecBase
import com.streamTracker.database.model.NotificationPlatform
import com.streamTracker.database.twitch.TwitchBotDAO
import com.streamTracker.database.twitch.TwitchBotService
import com.streamTracker.database.twitch.TwitchUserRelModel
import spock.lang.Shared

class TwitchBotServiceDatabaseSpec extends DatabaseSpecBase {

    @Shared
    TwitchBotService twitchBotService

    def setupSpec() {
        this.twitchBotService = new TwitchBotService(new TwitchBotDAO(this.sessionFactory))
    }

    void "Should get streamer models from database"() {
        when:
        def models = this.twitchBotService.getStreamerModels()

        then:
        models.size() == 2
        for (def model : models) {
            if (model.getStreamName() == "S1") {
                assert model.getUsers().size() == 1
                assert model.getUsers().get(0).getId() == 1
                assert !model.getUsers().get(0).isEnableStreamPredict()
                assert !model.isRecordStream()
            } else {
                assert model.getUsers().size() == 3
                assert model.isRecordStream()
                assert model.getUsers().any { it -> it.isEnableStreamPredict() }
            }
        }
    }

    void "Should insert new user in database"() {
        given:
        def user = TwitchUserRelModel.builder()
                .streamName(streamName)
                .userId(userId)
                .notificationPlatform(NotificationPlatform.DISCORD)
                .build()

        when:
        this.twitchBotService.addUser(user)

        then:
        def models = this.twitchBotService.getStreamerModels()
        def model = models.findAll { it -> it.getStreamName() == streamName }.get(0)
        model.getUsers().any { it -> it.getId() == userId }

        where:
        streamName | userId
        "S1"       | 1
        "S2"       | 1
    }

    void "Should not insert duplicate user"() {
        given:
        def user = TwitchUserRelModel.builder()
                .streamName("S1")
                .userId(3)
                .notificationPlatform(NotificationPlatform.DISCORD)
                .build()
        def originalSize = this.twitchBotService.getStreamerModels().get(0).getUsers().size()

        when:
        this.twitchBotService.addUser(user)
        this.twitchBotService.addUser(user)

        then:
        this.twitchBotService.getStreamerModels().get(0).getUsers().size() == originalSize + 1
    }

    void "Should create new stream model"() {
        given:
        def user = TwitchUserRelModel.builder()
                .streamName("S3")
                .userId(2)
                .notificationPlatform(NotificationPlatform.DISCORD)
                .build()
        def originalSize = this.twitchBotService.getStreamerModels().size()

        when:
        this.twitchBotService.addUser(user)

        then:
        this.twitchBotService.getStreamerModels().size() == originalSize + 1
        def model = this.twitchBotService.getStreamerModels().findAll { it -> it.getStreamName() == "S3" }.get(0)
        model.getUsers().size() == 1
    }
}
