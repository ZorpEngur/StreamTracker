package com.streamTracker.notification

import com.streamTracker.ApplicationProperties
import com.streamTracker.database.model.NotificationPlatform
import com.streamTracker.database.model.UserDatabaseModel
import com.streamTracker.database.user.UserService
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.requests.restaction.CacheRestAction
import spock.lang.Specification

import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class DiscordBotSpec extends Specification {

    void "Test message delay"() {
        given:
        def clock = Clock.fixed(Instant.parse("2025-03-18T10:00:00.000Z"), ZoneId.of("UTC"))
        JDA jda = Mock() {
            getStatus() >> JDA.Status.CONNECTED
            1 * openPrivateChannelById(_ as Long) >> Mock(CacheRestAction)
        }

        def bot = new DiscordBot(Mock(ApplicationProperties) {getMessageDelay() >> Duration.ofHours(1)}, clock,
                Mock(UserService) { getUser(_) >> Mock(UserDatabaseModel) { getDiscordId() >> 1; getName() >> "name" } })
        bot.jdaInstance = jda
        bot.destroyLock.set(true)

        def user1 = new StreamModel.UserModel(1, true, NotificationPlatform.DISCORD)
        def user2 = new StreamModel.UserModel(2, true, NotificationPlatform.DISCORD)
        user1.lastPing = LocalDateTime.MIN
        user2.lastPing = LocalDateTime.now(clock)
        def users = [user1, user2]

        when:
        bot.sendMessage(users, "message")

        then:
        users*.lastPing.each { it -> (it == LocalDateTime.now(clock)) }
    }
}
