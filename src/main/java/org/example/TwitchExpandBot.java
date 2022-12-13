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

    private final String CHANNEL = "zorpengur";
    private final TwitchClient twitchClient;

    public TwitchExpandBot(TwitchLiveBot twitchLiveBot) {
        TwitchClientBuilder clientBuilder = TwitchClientBuilder.builder();

        OAuth2Credential credential = new OAuth2Credential("zorpengurbot", "mgqjwj0paxya5h69ql6bajd1vz4gb1");


        twitchClient = clientBuilder
                .withDefaultEventHandler(SimpleEventHandler.class)
                .withEnableHelix(true)
                .withDefaultAuthToken(credential)
                .withEnableChat(true)
                .build();



        twitchClient.getEventManager().onEvent(ChannelMessageEvent.class, event -> {
            if (event.getUser().getName().equalsIgnoreCase("zorpengur") &&
                event.getMessage().startsWith("set")) {
                try {
                    FileWriter fileWriter = new FileWriter("streamNotifications.txt", true);
                    List<String> message = Arrays.stream(event.getMessage().split(" ")).collect(Collectors.toList());
                    message.removeIf(string -> string.strip().equals(""));
                    fileWriter.write(String.format("%s,%s,%s\n", message.get(1), message.get(2), message.get(3)));
                    fileWriter.close();
                    twitchLiveBot.loadFile();
                    twitchLiveBot.registerFeatures();
                } catch (IOException ex) {
                    log.error("failed to add new user", ex);
                }
            }
        });
    }

    public void registerFeatures() {
        twitchClient.getClientHelper().enableStreamEventListener(CHANNEL);
        twitchClient.getChat().joinChannel(CHANNEL);
    }
}
