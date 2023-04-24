package org.example

import com.github.twitch4j.TwitchClient

class TwitchExpandBotSpec extends BotTemplateSpec {

    private TwitchExpandBot bot

    def setup() {
        bot = new TwitchExpandBot(null, null, fileName)
        bot.@twitchClient = Stub(TwitchClient)
    }

    def "Expand bot add user test"() {
        given:
        createFile()
        String event = "set CHANNEL NAME DISCORD_ID"
        TwitchLiveBot liveBot = Mock(TwitchLiveBot) {
            addUser(*_) >> null
            registerFeatures() >> null
        }
        when:
        bot.addUser(event, liveBot)
        then:
        1 * liveBot.addUser(*_)
        1 * liveBot.registerFeatures()
        Scanner scanner = new Scanner(file)
        scanner.next() == "CHANNEL,NAME,DISCORD_ID".toLowerCase()
        !scanner.hasNext()
        scanner.close()
        cleanup:
        deleteFile()
    }
}
