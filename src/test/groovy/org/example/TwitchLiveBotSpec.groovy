package org.example

import com.github.twitch4j.TwitchClient

class TwitchLiveBotSpec extends BotTemplateSpec {

    private TwitchLiveBot bot

    def setup() {
        bot = new TwitchLiveBot(super.fileName)
        bot.@twitchClient = Stub(TwitchClient)
    }

    def cleanup() {
        deleteFile()
    }

    def "Live bot add user test"() {
        when:
        bot.addUser("channel", "name", "discordId")
        then:
        bot.@channelUsers.size() == 1
        bot.@channelUsers.get("channel").size() == 1
        bot.@channelUsers.get("channel").get(0).name == "name"
        bot.@channelUsers.get("channel").get(0).discordID == "discordId"
    }

    def "Live bot load no file"() {
        when:
        bot.loadFile()
        then:
        Scanner scanner = new Scanner(new File(fileName))
        !scanner.hasNext()
        scanner.close()
    }

    def "Live bot load existion file"() {
        given:
        createFile()
        file.append("channel,name,discordId")
        when:
        bot.loadFile()
        then:
        Scanner scanner = new Scanner(new File(fileName))
        scanner.hasNext()
        scanner.close()
        bot.@channelUsers.size() == 1
        bot.@channelUsers.get("channel").size() == 1
        bot.@channelUsers.get("channel").get(0).name == "name"
        bot.@channelUsers.get("channel").get(0).discordID == "discordId"
    }
}
