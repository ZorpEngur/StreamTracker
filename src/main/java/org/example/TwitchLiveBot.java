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
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
public class TwitchLiveBot {

    private static Map<String, List<BotUserModel>> channelUsers;
    private final TwitchClient twitchClient;
    private final Server server = null;

    public TwitchLiveBot() {
        loadFile();

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
            List<BotUserModel> users = new ArrayList<>(channelUsers.get(event.getChannel().getName().toLowerCase()));
            users.removeIf(u -> u.getLastPing().isBefore(LocalDateTime.now().minusMinutes(10)));
            Discord.sendMessage(users, event.getChannel().getName() + " went live! (title)");
            users.forEach(u -> u.setLastPing(LocalDateTime.now()));
        });

        twitchClient.getEventManager().onEvent(ChannelChangeGameEvent.class, event -> {
            try {
                wait(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            List<BotUserModel> users = new ArrayList<>(channelUsers.get(event.getChannel().getName().toLowerCase()));
            users.removeIf(u -> u.getLastPing().isBefore(LocalDateTime.now().minusMinutes(10)));
            Discord.sendMessage(users, event.getChannel().getName() + " went live! (game)");
            users.forEach(u -> u.setLastPing(LocalDateTime.now()));
        });

        twitchClient.getEventManager().onEvent(ChannelGoLiveEvent.class, event -> {
            List<BotUserModel> users = new ArrayList<>(channelUsers.get(event.getChannel().getName().toLowerCase()));
            users.removeIf(u -> u.getLastPing().isBefore(LocalDateTime.now().minusMinutes(10)));
            Discord.sendMessage(users, event.getChannel().getName() + " went live! (live)");
            users.forEach(u -> u.setLastPing(LocalDateTime.now()));
        });

        twitchClient.getEventManager().onEvent(ChannelMessageActionEvent.class, event -> {
            if ((event.getMessage().contains("NEW TITLE!") || event.getMessage().contains("NEW GAME!")) && event.getMessageEvent().getUserName().equalsIgnoreCase("TitleChange_Bot")) {
                List<BotUserModel> users = new ArrayList<>(channelUsers.get(event.getChannel().getName().toLowerCase()));
                users.removeIf(u -> u.getLastPing().isBefore(LocalDateTime.now().minusMinutes(10)));
                users.removeIf(u -> !event.getMessage().toLowerCase().contains(u.getName().toLowerCase()));
                Discord.sendMessage(users, event.getChannel().getName() + " went live! (live predict)");
                users.forEach(u -> u.setLastPing(LocalDateTime.now()));
            }
        });

//        twitchClient.getEventManager().onEvent(ChannelMessageEvent.class, event -> {
//            System.out.println("hi");
//        });
    }

    public void loadFile() {
        channelUsers = new HashMap<>();
        File file = new File("streamNotifications.txt");
        try {
            if (!file.createNewFile()) {
                Scanner scanner = new Scanner(file);
                while (scanner.hasNext()) {
                    String text = scanner.nextLine();
                    if (!text.isBlank()) {
                        String[] user = text.split(",");
                        if (channelUsers.containsKey(user[0])) {
                            channelUsers.get(user[0]).add(new BotUserModel(user[1], user[2]));
                        } else {
                            channelUsers.put(user[0], new ArrayList<>(List.of(new BotUserModel(user[1], user[2]))));
                        }
                    }
                }
                scanner.close();
            }
        } catch (Exception exception) {
            log.error("File loading failed!", exception);
        }
    }

    public void registerFeatures() {
        twitchClient.getClientHelper().enableStreamEventListener(channelUsers.keySet());
        for (String channel : channelUsers.keySet()) {
            twitchClient.getChat().joinChannel(channel);
        }
    }

    public void destroy(){
        twitchClient.close();
    }
}
