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

public class TwitchLiveBot {

    private final String CHANNEL = "pambaulettox";
    private LocalDateTime lastPing = LocalDateTime.MIN;
    private final TwitchClient twitchClient;
    private final Server server = null;

    public TwitchLiveBot() {
        TwitchClientBuilder clientBuilder = TwitchClientBuilder.builder();

        OAuth2Credential credential = new OAuth2Credential("zorpengurbot", "mgqjwj0paxya5h69ql6bajd1vz4gb1");

        //server = new Server(); not used, too complicated to do networking

        twitchClient = clientBuilder
                .withDefaultEventHandler(SimpleEventHandler.class)
                .withEnableHelix(true)
                .withDefaultAuthToken(credential)
                .withEnableChat(true)
                .build();

        twitchClient.getEventManager().onEvent(ChannelChangeTitleEvent.class, event -> {
            if (lastPing.isBefore(LocalDateTime.now().minusMinutes(10))){
                lastPing = LocalDateTime.now();
                Discord.sendMessage(event.getChannel().getName() + " went live! (title)");
            }
        });

        twitchClient.getEventManager().onEvent(ChannelChangeGameEvent.class, event -> {
            try {
                wait(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (lastPing.isBefore(LocalDateTime.now().minusMinutes(10))){
                lastPing = LocalDateTime.now();
                Discord.sendMessage(event.getChannel().getName() + " went live! (game)");
            }
        });

        twitchClient.getEventManager().onEvent(ChannelGoLiveEvent.class, event -> {
            if (lastPing.isBefore(LocalDateTime.now().minusMinutes(10))){
                lastPing = LocalDateTime.now();
                Discord.sendMessage(event.getChannel().getName() + " went live! (live)");
            }
        });

        twitchClient.getEventManager().onEvent(ChannelMessageActionEvent.class, event -> {
            if (event.getMessage().contains("live") && event.getMessage().contains("ZorpEngur") && event.getMessageEvent().getUserName().equalsIgnoreCase("TitleChange_Bot ")) {
                if (lastPing.isBefore(LocalDateTime.now().minusMinutes(10))){
                    lastPing = LocalDateTime.now();
                    Discord.sendMessage(event.getChannel().getName() + " went live! (message v1)");
                }
            }
        });

//        twitchClient.getEventManager().onEvent(ChannelMessageEvent.class, event -> {
//            System.out.println("hi");
//        });
    }

    public void registerFeatures() {
        twitchClient.getClientHelper().enableStreamEventListener(CHANNEL);
        twitchClient.getChat().joinChannel(CHANNEL);
    }
}
