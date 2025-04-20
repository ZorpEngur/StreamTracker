package com.streamTracker.notification

import com.streamTracker.database.model.NotificationPlatform
import com.streamTracker.database.model.UserDatabaseModel
import com.streamTracker.database.twitch.TwitchBotService
import com.streamTracker.database.twitch.TwitchUserRelModel
import com.streamTracker.database.user.UserService
import lombok.NonNull
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils
import spock.lang.Specification

import java.util.regex.Pattern

class CommandManagerSpec extends Specification {

    void "parser register command"() {
        given:
        UserService userService = Mock()
        def commandManager = new CommandManager(Mock(TwitchBotService), userService)

        when:
        commandManager.parseCommand(command, 1, "twitchName")

        then:
        1 * userService.saveUser(UserDatabaseModel.builder()
                .twitchId(twitchId)
                .discordId(resultDiscord)
                .name(resultName)
                .build())

        where:
        command                               | twitchId | resultName   | resultDiscord
        "register -discord:112233 -name:test" | 1        | "test"       | 112233
        "register -d:112233 -name:test"       | 1        | "test"       | 112233
        "register -d:112233"                  | 1        | "twitchName" | 112233
        "register -name:test"                 | 1        | "test"       | null
        "register -notme"                     | null     | "twitchName" | null
        "register"                            | 1        | "twitchName" | null
    }

    void "successfully parser set command"() {
        given:
        UserService userService = Mock() {
            getUser("First User") >> Mock(UserDatabaseModel) {
                getId() >> 1
                getName() >> "First User"
            }
            getTwitchUser(1) >> Mock(UserDatabaseModel) {
                getId() >> 2
                getName() >> "name"
            }
        }
        TwitchBotService twitchBotService = Mock()
        def commandManager = new CommandManager(twitchBotService, userService)

        when:
        commandManager.parseCommand(command, 1, "twitchName")

        then:
        1 * twitchBotService.addUser(TwitchUserRelModel.builder()
                .streamName(streamName)
                .userId(userId)
                .recordStream(recordStream)
                .streamPrediction(streamPredict)
                .notificationPlatform(notificationPlatform)
                .build())

        where:
        command                                            | streamName | userId | recordStream | streamPredict | notificationPlatform
        "set -u:First User -t:test -r -predict -p:DISCORD" | "test"     | 1      | true         | true          | NotificationPlatform.DISCORD
        "set -t:test -p:discord -r"                        | "test"     | 2      | true         | false         | NotificationPlatform.DISCORD
        "set -p:discord -t:test"                           | "test"     | 2      | false        | false         | NotificationPlatform.DISCORD
    }

    void "fail parser set command"() {
        given:
        UserService userService = Mock() {
            getTwitchUser(1) >> Mock(UserDatabaseModel) {
                getId() >> 2
                getName() >> "name"
            }
        }
        TwitchBotService twitchBotService = Mock()
        def commandManager = new CommandManager(twitchBotService, userService)

        when:
        commandManager.parseCommand(command, twitchId, "name")

        then:
        0 * twitchBotService.addUser(_)
        def ex = thrown(CommandException)
        ex.getMessage() =~ message

        where:
        command                                                 | twitchId | message
        "set -u:Fake User -t:test -r:yes -predict:1 -p:DISCORD" | 1        | "User not found."
        "set -t:test -r:yes -predict:1 -p:DISCORD"              | 2        | "You are not registered."
        "set -t:test -r:yes -predict:1 -p:Invalid Platform"     | 1        | "Invalid notification platform."
        "set -t:test -r:yes -predict:1"                         | 1        | "Notification platform not provided."
        "set -r:yes -predict:1 -p:discord"                      | 1        | "Twitch channel not provided."
        "set -t: -p:discord"                                    | 1        | "Twitch channel not provided."
        "set"                                                   | 1        | "not provided|not registered"
    }

    void "ignore invalid commands"() {
        given:
        def commandManager = new CommandManager(Mock(TwitchBotService), Mock(UserService))

        expect:
        commandManager.parseCommand(command, 1, "name") == null

        where:
        command        | _
        ""             | _
        "test"         | _
        "setsomething" | _
    }

    void "generated test"() {
        given:
        String response = null
        boolean thrown = true
        UserService userService = Mock() {
            getTwitchUser(1) >> Mock(UserDatabaseModel) {
                getId() >> 2
            }
            getUser("name") >> Mock(UserDatabaseModel) {
                getId() >> 2
            }
        }
        TwitchBotService twitchBotService = Mock()
        CommandManager commandManager = new CommandManager(twitchBotService, userService)

        and:
        if (command.matches("(?i)^set\\s+-.*")
                && matchesGroup(command, "u(ser)?(?::(?<value>.*))", "name", true)
                && matchesGroup(command, "p(latform)?(?::(?<value>.*))", "discord", true)
                && matchesGroup(command, "t(witch)?(?::(?<value>.*))", ".+", true)) {
            1 * twitchBotService.addUser(_)
            response = "Notification set for user "
            thrown = false
        } else if (command.matches("(?i)^register(?:\\s+-.*|\$)")
                && matchesGroup(command, "d(iscord)?(?::(?<value>.*)|\$)", "\\d+", false)) {
            1 * userService.saveUser(_)
            response = "User registered. "
            thrown = false
        } else if (command.matches("(?i)^help(?:\\s+-.*|\$)")) {
            def all = command.matches("(?i)^help(?:\\s+-\$|\$)");
            if (all || command.matches("(?i)^help\\s+-register")) {
                response = "REGISTER: creates new user for this bot. Parameters:"
                thrown = false
            }
            if (all || command.matches("(?i)^help\\s+-set")) {
                response = "SET: adds new notification event for registered user. Parameters:"
                thrown = false
            }
            if (all || command.matches("(?i)^help\\s+-help")) {
                response = "HELP: displays information about the commands. For seeing specific command use 'help' -<command name>."
                thrown = false
            }
        } else {
            0 * twitchBotService.addUser(_)
            0 * userService.saveUser(_)
        }

        expect:
        try {
            def result = commandManager.parseCommand(command, 1, "name")
            assert result =~ response
        } catch (CommandException ignored) {
            assert thrown
        }

        where:
        command << getRandomCommands()
    }

    private static List<String> getRandomCommands() {
        List<String> commands = []
        Random random = new Random()
        for (int i = 0; i < 10000; i++) {
            def command = switch (random.nextInt(4)) {
                case 0 -> "register"
                case 1 -> "set"
                case 2 -> "help"
                default -> RandomStringUtils.randomAscii(0, 6)
            }
            def length = random.nextInt(8)
            while (length > 0) {
                command = command + " " + RandomStringUtils.randomAscii(0, 6)
                length--
            }
            commands.add(command)
        }
        return commands
    }

    private static boolean matchesGroup(@NonNull String input, @NonNull String pattern, @NonNull String value, boolean hasToMatch) {
        def matches = Arrays.asList(input.split(" -")).stream()
                .map { Pattern.compile("(?i)^" + pattern).matcher(it) }
                .filter { it.find() }
                .toList()
        return matches.every { it.group("value") != null && it.group("value").matches("(?i)" + value) }
                && !(matches.isEmpty() && hasToMatch)
    }
}
