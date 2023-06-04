package org.example;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Bot that handles user operations.
 */
@Slf4j @RequiredArgsConstructor
public class TwitchExpandBot {

    /**
     * Live bot onto which edits are applied.
     */
    private final TwitchLiveBot twitchLiveBot;

    /**
     * Channel name where bot is running.
     */
    private final String channel;

    /**
     * Name of the file with users.
     */
    private final String file;

    /**
     * Client of the bot.
     */
    private TwitchClient twitchClient = null;

    /**
     * Starts the bot.
     */
    public void startBot() {
        TwitchClientBuilder clientBuilder = TwitchClientBuilder.builder();

        OAuth2Credential credential = new OAuth2Credential("zorpengurbot", "mgqjwj0paxya5h69ql6bajd1vz4gb1");

        this.twitchClient = clientBuilder
                .withDefaultEventHandler(SimpleEventHandler.class)
                .withEnableHelix(true)
                .withDefaultAuthToken(credential)
                .withEnableChat(true)
                .withChatAccount(credential)
                .build();

        this.twitchClient.getEventManager().onEvent(ChannelMessageEvent.class, event -> {
            if (event.getUser().getName().equalsIgnoreCase("zorpengur") && event.getMessage().startsWith("set")) {
                addUser(event.getMessage());
            }
        });
    }

    /**
     * Adds new user to the file and user map in TwitchLiveBot.
     *
     * @param message Message in format: CHANNEL NAME DISCORD_ID
     */
    public void addUser(String message){
        try {
            FileWriter fileWriter = new FileWriter(this.file, true);
            List<String> data = Arrays.stream(message.toLowerCase().split(" ")).collect(Collectors.toList());
            data.removeIf(string -> string.strip().equals(""));
            fileWriter.write(String.format("%s,%s,%s\n", data.get(1), data.get(2), data.get(3)));
            fileWriter.flush();
            fileWriter.close();
            this.twitchLiveBot.addUser(data.get(1), data.get(2), data.get(3));
            this.twitchLiveBot.registerFeatures();
            this.twitchClient.getChat().sendMessage(this.channel, "Added!");
        } catch (IOException ex) {
            log.error("failed to add new user", ex);
        }
    }

    /**
     * Connects bot to the channel.
     */
    public void registerFeatures() {
        this.twitchClient.getClientHelper().enableStreamEventListener(this.channel);
        this.twitchClient.getChat().joinChannel(this.channel);
    }

    /**
     * Destroys the bot.
     */
    public void destroy(){
        this.twitchClient.close();
    }
}
