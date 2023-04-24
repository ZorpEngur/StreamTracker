package org.example;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.channel.ChannelMessageActionEvent;
import com.github.twitch4j.events.ChannelChangeGameEvent;
import com.github.twitch4j.events.ChannelChangeTitleEvent;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.*;

@Slf4j @RequiredArgsConstructor
public class TwitchLiveBot {

    private final Map<String, List<BotUserModel>> channelUsers = new HashMap<>();
    private TwitchClient twitchClient;

    private final String fileName;

    public void startBot() {
        loadFile();

        TwitchClientBuilder clientBuilder = TwitchClientBuilder.builder();

        OAuth2Credential credential = new OAuth2Credential("zorpengurbot", "mgqjwj0paxya5h69ql6bajd1vz4gb1");

        twitchClient = clientBuilder
                .withDefaultEventHandler(SimpleEventHandler.class)
                .withEnableHelix(true)
                .withDefaultAuthToken(credential)
                .withEnableChat(true)
                .build();

        registerEvents();
    }

    private void registerEvents() {
        twitchClient.getEventManager().onEvent(ChannelGoLiveEvent.class, event -> {
            System.out.println("pog");
            Discord.sendMessage(new ArrayList<>(channelUsers.get(event.getChannel().getName().toLowerCase())), event.getChannel().getName() + " went live! (live)");
        });

        twitchClient.getEventManager().onEvent(ChannelChangeTitleEvent.class, event -> {
            Discord.sendMessage(new ArrayList<>(channelUsers.get(event.getChannel().getName().toLowerCase())), event.getChannel().getName() + " went live! (title)");
        });

        twitchClient.getEventManager().onEvent(ChannelChangeGameEvent.class, event -> {
            try {
                wait(1000); //delay for concurrent title and game change
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Discord.sendMessage(new ArrayList<>(channelUsers.get(event.getChannel().getName().toLowerCase())), event.getChannel().getName() + " went live! (game)");
        });

        twitchClient.getEventManager().onEvent(ChannelMessageActionEvent.class, event -> {
            if ((event.getMessage().contains("NEW TITLE!") || event.getMessage().contains("NEW GAME!") || event.getMessage().contains("has gone live")) && event.getMessageEvent().getUserName().equalsIgnoreCase("TitleChange_Bot")) {
                List<BotUserModel> users = new ArrayList<>(channelUsers.get(event.getChannel().getName().toLowerCase()));
                users.removeIf(u -> !event.getMessage().toLowerCase().contains(u.getName().toLowerCase()));
                Discord.sendMessage(users, event.getChannel().getName() + " went live! (live predict)");
            }
        });
    }

    public void loadFile() {
        File file = new File(fileName);
        try {
            if (!file.createNewFile()) {
                Scanner scanner = new Scanner(file);
                while (scanner.hasNext()) {
                    String text = scanner.nextLine();
                    if (!text.isBlank()) {
                        String[] user = text.split(",");
                        addUser(user[0], user[1], user[2]);
                    }
                }
                scanner.close();
            }
        } catch (Exception exception) {
            log.error("File loading failed!", exception);
        }
    }

    public void addUser(String channel, String name, String discordID) {
        if (channelUsers.containsKey(channel)) {
            channelUsers.get(channel).add(new BotUserModel(name, discordID));
        } else {
            channelUsers.put(channel, new ArrayList<>(List.of(new BotUserModel(name, discordID))));
        }
    }

    public void registerFeatures() {
        twitchClient.getClientHelper().enableStreamEventListener(channelUsers.keySet());
        for (String channel : channelUsers.keySet()) {
            if (!twitchClient.getChat().getChannels().contains(channel)) {
                twitchClient.getChat().joinChannel(channel);
            }
        }
    }

    public void destroy(){
        twitchClient.close();
    }
}
