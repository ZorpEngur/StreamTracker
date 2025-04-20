package com.streamTracker.notification;

import com.streamTracker.database.model.NotificationPlatform;
import com.streamTracker.database.model.UserDatabaseModel;
import com.streamTracker.database.twitch.TwitchBotService;
import com.streamTracker.database.twitch.TwitchUserRelModel;
import com.streamTracker.database.user.UserService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Defines available commands of this bot.
 */
@Slf4j
@RequiredArgsConstructor
public class CommandManager {

    /**
     * Database service for twitch.
     */
    @NonNull
    private final TwitchBotService twitchBotService;

    /**
     * Database service for users.
     */
    @NonNull
    private final UserService userService;

    /**
     * Tries to parse potential command from message.
     *
     * @param command  Message containing the command.
     * @param twitchId Twitch ID of the user sending this message.
     * @param name     Name of the user sending this message.
     * @return Message with successful result of the command or {@code null} if message was determined not to be command.
     * @throws CommandException Error thrown when the command has some syntactic errors.
     */
    @Nullable
    public String parseCommand(@NonNull String command, @Nullable Long twitchId, @NonNull String name) throws CommandException {
        Iterator<String> iterator = Arrays.stream(command.split("\\s+-")).iterator();
        if (iterator.hasNext()) {
            return switch (iterator.next().toLowerCase()) {
                case "set" -> set(iterator, twitchId);
                case "register" -> register(iterator, twitchId, name);
                case "help" -> help(iterator);
                default -> null;
            };
        }
        return null;
    }

    /**
     * Registers new user for this bot.
     *
     * @param parameters Parameters of the command.
     * @param twitchId   Twitch ID of the user sending this command.
     * @param name       Name of the user sending this command.
     * @return Message with successful result of the command.
     * @throws CommandException Error thrown when the command has some syntactic errors.
     */
    @NonNull
    private String register(@NonNull Iterator<String> parameters, @Nullable Long twitchId, @NonNull String name) throws CommandException {
        UserDatabaseModel.UserDatabaseModelBuilder builder = UserDatabaseModel.builder()
                .name(name)
                .twitchId(twitchId);

        while (parameters.hasNext()) {
            String[] param = parameters.next().split(":", 2);
            switch (Parameters.fromValue(param[0].toLowerCase())) {
                case DISCORD:
                    if (param.length != 2 || !param[1].matches("^\\d+$"))
                        throw new CommandException("Invalid discord ID.");
                    builder.discordId(Long.parseLong(param[1]));
                    break;
                case NAME:
                    if (param.length == 2 && !param[1].isBlank()) {
                        builder.name(param[1]);
                    }
                    break;
                case NOTME:
                    builder.twitchId(null);
                    break;
            }
        }
        UserDatabaseModel user = builder.build();
        if (this.userService.userExists(user)) {
            return "This user already exists.";
        }
        this.userService.saveUser(user);
        log.info("New user registered. {}", user);
        return "User registered. " + user.getName();
    }

    /**
     * Sets new notification event for existing user.
     *
     * @param parameters Parameters of the command.
     * @param twitchId   Twitch ID of the user sending this command.
     * @return Message with successful result of the command.
     * @throws CommandException Error thrown when the command has some syntactic errors.
     */
    @NonNull
    private String set(@NonNull Iterator<String> parameters, @Nullable Long twitchId) throws CommandException {
        TwitchUserRelModel.TwitchUserRelModelBuilder builder = TwitchUserRelModel.builder();
        String userName = null;
        String twitchName = null;
        String platformName = null;

        if (twitchId != null) {
            UserDatabaseModel user = this.userService.getTwitchUser(twitchId);
            if (user != null && user.getId() != null) {
                builder.userId(user.getId());
                userName = user.getName();
            }
        }

        while (parameters.hasNext()) {
            String[] param = parameters.next().split(":", 2);
            switch (Parameters.fromValue(param[0].toLowerCase())) {
                case USER:
                    if (param.length == 2) {
                        UserDatabaseModel user = this.userService.getUser(param[1]);
                        if (user == null || user.getId() == null)
                            throw new CommandException("User not found.");
                        builder.userId(user.getId());
                        userName = user.getName();
                    }
                    break;
                case TWITCH:
                    if (param.length == 2 && !param[1].isBlank()) {
                        builder.streamName(param[1]);
                        twitchName = param[1];
                    }
                    break;
                case RECORD:
                    builder.recordStream(true);
                    break;
                case PREDICT:
                    builder.streamPrediction(true);
                    break;
                case PLATFORM:
                    if (param.length == 2) {
                        NotificationPlatform platform = NotificationPlatform.fromName(param[1]);
                        if (platform == null)
                            throw new CommandException("Invalid notification platform.");
                        builder.notificationPlatform(platform);
                        platformName = platform.name();
                    }
                    break;
            }
        }

        if (userName == null) {
            throw new CommandException("You are not registered.");
        } else if (twitchName == null) {
            throw new CommandException("Twitch channel not provided.");
        } else if (platformName == null) {
            throw new CommandException("Notification platform not provided.");
        }
        this.twitchBotService.addUser(builder.build());
        log.info("Set new notification event. {}", builder.build().toString());
        return "Notification set for user " + userName + " for channel " + twitchName + " using platform " + platformName;
    }

    /**
     * Returns information about available commands.
     *
     * @param parameters Parameters of the command.
     * @return Message with successful result of the command.
     * @throws CommandException Error thrown when the command has some syntactic errors.
     */
    @NonNull
    private String help(@NonNull Iterator<String> parameters) throws CommandException {
        StringBuilder answer = new StringBuilder();
        boolean all = !parameters.hasNext();
        String command = all ? null : parameters.next().strip();
        if (all || command.equals("register")) {
            answer.append("REGISTER: creates new user for this bot.\n");
            if (!all) {
                answer.append("Parameters:\n");
                answer.append(Parameters.DISCORD);
                answer.append(Parameters.NAME);
                answer.append(Parameters.NOTME);
            }
        }
        if (all || command.equals("set")) {
            answer.append("SET: adds new notification event for registered user.\n");
            if (!all) {
                answer.append("Parameters:\n");
                answer.append(Parameters.USER);
                answer.append(Parameters.TWITCH);
                answer.append(Parameters.PLATFORM);
                answer.append(Parameters.RECORD);
                answer.append(Parameters.PREDICT);
            }
        }
        if (all || command.equals("help")) {
            answer.append("HELP: displays information about the commands. For seeing specific command use 'help' -<command name>.\n");
        }
        if (answer.isEmpty()) {
            throw new CommandException("Command not found. Use 'help' to see all commands.");
        }
        return answer.toString();
    }

    /**
     * Definition of all available parameters form commands.
     */
    @Getter
    @AllArgsConstructor
    private enum Parameters {
        DISCORD("discord", "d", true, "Your Discord ID."),
        USER("user", "u", true, "Name of the user, you are setting this for. May be omitted if setting for yourself."),
        TWITCH("twitch", "t", true, "Name of the twitch channel to get notified for."),
        RECORD("record", "r", false, "If you want the stream to be recorded."),
        PLATFORM("platform", "p", true, "Platform where you will be notified: " + Arrays.toString(NotificationPlatform.values())),
        NOTME("notme", null, false, "Used if you don't want to be associated with this user."),
        PREDICT("predict", null, false, "If you want to be notified of the stream in advance. Might not work reliably. !EXPERIMENTAL FEATURE!"),
        NAME("name", null, true, "Name of the user so he can be referenced later. If not provided, your name is used."),
        VOID("VOID", null, false, "VOID");

        /**
         * Full name of the parameter.
         */
        @NonNull
        private final String fullName;

        /**
         * Abbreviation of the parameter if available.
         */
        @Nullable
        private final String abbreviation;

        /**
         * Flag if the parameter has input value.
         */
        private final boolean value;

        /**
         * Description of the command.
         */
        @NonNull
        private final String description;

        @Override
        public String toString() {
            String result = "-" + this.fullName;
            if (abbreviation != null) {
                result = result + "/-" + this.abbreviation;
            }
            if (this.value) {
                result = result + ":<value>";
            }
            return result + " " + this.description + "\n";
        }

        /**
         * Retrieves the parameter from the name or abbreviation.
         *
         * @param input Name or abbreviation of the parameter.
         * @return The parameter or {@link #VOID} if no parameter found.
         */
        @NonNull
        public static CommandManager.Parameters fromValue(@NonNull String input) {
            for (Parameters command : Parameters.values()) {
                if (input.equals(command.getFullName()) || input.equals(command.getAbbreviation())) {
                    return command;
                }
            }
            return Parameters.VOID;
        }
    }
}
