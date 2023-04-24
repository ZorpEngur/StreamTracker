package org.example;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import lombok.extern.slf4j.Slf4j;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class TwitchExpandBot {

    private final String channel;
    private TwitchClient twitchClient = null;
    private final String file;
    private final TwitchLiveBot twitchLiveBot;

    public TwitchExpandBot(TwitchLiveBot twitchLiveBot, String channel, String file) {
        this.twitchLiveBot = twitchLiveBot;
        this.channel = channel;
        this.file = file;
    }

    public void startBot() {
        System.out.println("WTF");
        TwitchClientBuilder clientBuilder = TwitchClientBuilder.builder();

        OAuth2Credential credential = new OAuth2Credential("zorpengurbot", "mgqjwj0paxya5h69ql6bajd1vz4gb1");

        twitchClient = clientBuilder
                .withDefaultEventHandler(SimpleEventHandler.class)
                .withEnableHelix(true)
                .withDefaultAuthToken(credential)
                .withEnableChat(true)
                .withChatAccount(credential)
                .build();

        twitchClient.getEventManager().onEvent(ChannelMessageEvent.class, event -> {
            if (event.getUser().getName().equalsIgnoreCase("zorpengur") && event.getMessage().startsWith("set")) {
                addUser(event.getMessage(), twitchLiveBot);
            }
        });
    }

    public void addUser(String message, TwitchLiveBot twitchLiveBot){
        try {
            FileWriter fileWriter = new FileWriter(file, true);
            List<String> data = Arrays.stream(message.toLowerCase().split(" ")).collect(Collectors.toList());
            data.removeIf(string -> string.strip().equals(""));
            fileWriter.write(String.format("%s,%s,%s\n", data.get(1), data.get(2), data.get(3)));
            fileWriter.flush();
            fileWriter.close();
            twitchLiveBot.addUser(data.get(1), data.get(2), data.get(3));
            twitchLiveBot.registerFeatures();
            twitchClient.getChat().sendMessage(channel, "Added!");
        } catch (IOException ex) {
            log.error("failed to add new user", ex);
        }
    }

    public void registerFeatures() {
        twitchClient.getClientHelper().enableStreamEventListener(channel);
        twitchClient.getChat().joinChannel(channel);
    }

    public void destroy(){
        twitchClient.close();
    }
}
