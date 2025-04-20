package com.streamTracker.notification;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.streamTracker.ApplicationProperties;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Bot that handles user operations.
 */
@Slf4j
@RequiredArgsConstructor
public class TwitchCommandBot {

    /**
     * Client of the bot.
     */
    @Nullable
    private TwitchClient twitchClient;

    /**
     * Properties for the application.
     */
    @NonNull
    private final ApplicationProperties properties;

    /**
     * Parser of the command.
     */
    @NonNull
    private final CommandManager commandManager;

    /**
     * Starts the bot.
     */
    @NonNull
    public TwitchCommandBot startBot() {
        TwitchClientBuilder clientBuilder = TwitchClientBuilder.builder();

        OAuth2Credential credential = new OAuth2Credential(this.properties.getTwitchName(), this.properties.getTwitchToken());

        this.twitchClient = clientBuilder
                .withDefaultEventHandler(SimpleEventHandler.class)
                .withEnableHelix(true)
                .withDefaultAuthToken(credential)
                .withEnableChat(true)
                .withChatAccount(credential)
                .build();

        registerEvents();
        registerFeatures();
        return this;
    }

    /**
     * Registers events of this bot.
     */
    private void registerEvents() {
        Objects.requireNonNull(this.twitchClient).getEventManager().onEvent(ChannelMessageEvent.class, event -> {
            if (event.getUser().getName().equalsIgnoreCase(this.properties.getManageChannel())) {
                try {
                    String response = this.commandManager.parseCommand(event.getMessage(), Long.valueOf(event.getUser().getId()), event.getUser().getName());
                    if (response != null) {
                        this.twitchClient.getChat().sendMessage(event.getChannel().getName(), response);
                    }
                } catch (CommandException ex) {
                    this.twitchClient.getChat().sendMessage(event.getChannel().getName(), ex.getMessage());
                } catch (Exception ex) {
                    log.warn("Error in command:", ex);
                    this.twitchClient.getChat().sendMessage(event.getChannel().getName(), "Error has occurred. :/");
                }
            }
        });
    }

    /**
     * Connects bot to the channel.
     */
    private void registerFeatures() {
        Objects.requireNonNull(this.twitchClient).getClientHelper().enableStreamEventListener(this.properties.getTwitchName());
    }

    /**
     * Destroys the bot.
     */
    public void destroy() {
        if (this.twitchClient != null) {
            this.twitchClient.close();
        }
    }
}
