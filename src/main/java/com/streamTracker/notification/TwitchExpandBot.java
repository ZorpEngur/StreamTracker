package com.streamTracker.notification;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.streamTracker.database.model.UserRegistrationModel;
import com.streamTracker.database.properties.PropertiesService;
import com.streamTracker.database.twitch.TwitchBotService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Bot that handles user operations.
 */
@Slf4j
public class TwitchExpandBot {

    /**
     * Live bot onto which edits are applied.
     */
    @NonNull
    private final TwitchLiveBot twitchLiveBot;

    /**
     * Channel name where bot is running.
     */
    @NonNull
    private final String channel;

    /**
     * Client of the bot.
     */
    @Nullable
    private TwitchClient twitchClient = null;

    /**
     * Database service for twitch.
     */
    @NonNull
    private final TwitchBotService twitchBotService = TwitchBotService.getInstance();

    /**
     * Database service for properties.
     */
    @NonNull
    private final PropertiesService propertiesService = PropertiesService.getInstance();

    public TwitchExpandBot(@NonNull TwitchLiveBot twitchLiveBot) {
        this.twitchLiveBot = twitchLiveBot;
        this.channel = this.propertiesService.getManageChannel();
    }

    /**
     * Starts the bot.
     */
    public void startBot() {
        TwitchClientBuilder clientBuilder = TwitchClientBuilder.builder();

        OAuth2Credential credential = new OAuth2Credential(this.propertiesService.getTwitchName(), this.propertiesService.getTwitchToken());

        this.twitchClient = clientBuilder
                .withDefaultEventHandler(SimpleEventHandler.class)
                .withEnableHelix(true)
                .withDefaultAuthToken(credential)
                .withEnableChat(true)
                .withChatAccount(credential)
                .build();

        this.twitchClient.getEventManager().onEvent(ChannelMessageEvent.class, event -> {
            if (event.getUser().getName().equalsIgnoreCase(this.channel) && event.getMessage().startsWith("set")) {
                addUser(event.getMessage());
            }
        });

        registerFeatures();
    }

    /**
     * Adds new user to database and reloads users in {@link TwitchLiveBot}.
     *
     * @param message Message in format: CHANNEL USER_NAME DISCORD_ID
     */
    public void addUser(@NonNull String message) {
        List<String> data = Arrays.stream(message.toLowerCase().split(" ")).collect(Collectors.toList());
        data.removeIf(String::isBlank);
        UserRegistrationModel newUser = UserRegistrationModel.builder()
                .streamName(data.get(1))
                .userName(data.get(2))
                .discordId(Long.parseLong(data.get(3)))
                .streamPrediction(parseBoolean(data.get(4)))
                .recordStream(parseBoolean(data.get(5)))
                .build();
        this.twitchBotService.addUser(newUser);
        this.twitchLiveBot.loadUsers();
        this.twitchClient.getChat().sendMessage(this.channel, "Added!");
        log.debug("Added user {}", data);
    }

    private boolean parseBoolean(@NonNull String input) {
        return input.equalsIgnoreCase("true") || input.equalsIgnoreCase("1") || input.equalsIgnoreCase("yes");
    }

    /**
     * Connects bot to the channel.
     */
    private void registerFeatures() {
        this.twitchClient.getClientHelper().enableStreamEventListener(this.channel);
        this.twitchClient.getChat().joinChannel(this.channel);
    }

    /**
     * Destroys the bot.
     */
    public void destroy() {
        this.twitchClient.close();
    }
}
