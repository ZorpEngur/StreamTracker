package org.example;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.channel.ChannelMessageActionEvent;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.events.ChannelChangeGameEvent;
import com.github.twitch4j.events.ChannelChangeTitleEvent;
import com.github.twitch4j.events.ChannelGoLiveEvent;

import java.time.LocalDateTime;

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
            System.out.println("hi");
        });
    }

    public void registerFeatures() {
        twitchClient.getClientHelper().enableStreamEventListener(CHANNEL);
        twitchClient.getChat().joinChannel(CHANNEL);
    }
}
