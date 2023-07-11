package com.zorpengur.notification

import com.github.twitch4j.TwitchClient

class TwitchExpandBotSpec extends BotTemplateSpec {

    private TwitchExpandBot bot

    def setup() {
        def liveBot = Mock(TwitchLiveBot) {
            addUser(*_) >> null
            registerFeatures() >> null
        }
        bot = new TwitchExpandBot(liveBot, null, fileName)
        bot.@twitchClient = Stub(TwitchClient)
    }

    def "Expand bot add user test"() {
        given:
        createFile()
        String event = "set CHANNEL NAME DISCORD_ID"
        when:
        bot.addUser(event)
        then:
        1 * bot.@twitchLiveBot.addUser(*_)
        1 * bot.@twitchLiveBot.registerFeatures()
        Scanner scanner = new Scanner(file)
        scanner.next() == "CHANNEL,NAME,DISCORD_ID".toLowerCase()
        !scanner.hasNext()
        scanner.close()
        cleanup:
        deleteFile()
    }
}
