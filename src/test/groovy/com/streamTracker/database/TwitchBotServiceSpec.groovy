package com.streamTracker.database

import com.streamTracker.SpecBase
import com.streamTracker.database.model.UserRegistrationModel
import com.streamTracker.database.twitch.TwitchBotService

class TwitchBotServiceSpec extends SpecBase {

    void "Should get streamer models from database"() {
        when:
        def models = TwitchBotService.getInstance().getStreamerModels()

        then:
        models.size() == 2
        for (def model : models) {
            if (model.getStreamName() == "S1") {
                assert model.getUsers().size() == 1
                assert model.getUsers().get(0).getDiscordId() == 453262634536816283
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
        def user = UserRegistrationModel.builder()
                .streamName(streamName)
                .discordId(discordId)
                .userName("Name")
                .build()

        when:
        TwitchBotService.getInstance().addUser(user)

        then:
        def models = TwitchBotService.getInstance().getStreamerModels()
        def model = models.findAll { it -> it.getStreamName() == streamName }.get(0)
        model.getUsers().any { it -> it.getName() == "Name" && it.getDiscordId() == discordId }

        where:
        streamName | discordId
        "S1"       | 123456
        "S2"       | 123456
    }

    void "Should not insert duplicate user"() {
        given:
        def user = UserRegistrationModel.builder()
                .streamName("S1")
                .discordId(123456)
                .userName("Name")
                .build()
        def originalSize = TwitchBotService.getInstance().getStreamerModels().get(0).getUsers().size()

        when:
        TwitchBotService.getInstance().addUser(user)
        TwitchBotService.getInstance().addUser(user)

        then:
        TwitchBotService.getInstance().getStreamerModels().get(0).getUsers().size() == originalSize + 1
    }

    void "Should create new stream model"() {
        given:
        def user = UserRegistrationModel.builder()
                .streamName("S3")
                .discordId(123456)
                .userName("Name")
                .build()
        def originalSize = TwitchBotService.getInstance().getStreamerModels().size()

        when:
        TwitchBotService.getInstance().addUser(user)

        then:
        TwitchBotService.getInstance().getStreamerModels().size() == originalSize + 1
        def model = TwitchBotService.getInstance().getStreamerModels().findAll {it -> it.getStreamName() == "S3"}.get(0)
        model.getUsers().size() == 1
        model.getUsers().get(0).getName() == "Name"
    }
}
