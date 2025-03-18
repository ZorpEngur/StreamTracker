package com.streamTracker.notification

import com.streamTracker.ApplicationProperties
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

        def bot = new DiscordBot(Mock(ApplicationProperties) { getMessageDelay() >> Duration.ofHours(1) }, clock)
        bot.jdaInstance = jda
        bot.destroyLock.set(true)

        def user1 = new StreamModel.UserModel("user", 1L, true)
        def user2 = new StreamModel.UserModel("user", 2L, true)
        user1.lastPing = LocalDateTime.MIN
        user2.lastPing = LocalDateTime.now(clock)
        def users = [user1, user2]

        when:
        bot.sendMessage(users, "message")

        then:
        users*.lastPing.each { it -> (it == LocalDateTime.now(clock)) }
    }
}
