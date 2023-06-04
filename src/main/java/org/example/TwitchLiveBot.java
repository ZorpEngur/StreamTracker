package org.example;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.channel.ChannelMessageActionEvent;
import com.github.twitch4j.events.ChannelChangeGameEvent;
import com.github.twitch4j.events.ChannelChangeTitleEvent;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.*;

/**
 * Bot that sends notifiactions.
 */
@Slf4j @RequiredArgsConstructor
public class TwitchLiveBot {

    /**
     * Map of the channels with the users registered for their notification.
     */
    private final Map<String, List<BotUserModel>> channelUsers = new HashMap<>();

    /**
     * Client of the bot.
     */
    private TwitchClient twitchClient;

    /**
     * Name of the file with the users.
     */
    private final String fileName;

    /**
     * Starts twitch bot and register event handlers.
     * @see TwitchLiveBot#registerFeatures() Needs to be called to join bot to channels.
     */
    public void startBot() {
        loadFile();

        TwitchClientBuilder clientBuilder = TwitchClientBuilder.builder();

        OAuth2Credential credential = new OAuth2Credential("zorpengurbot", "mgqjwj0paxya5h69ql6bajd1vz4gb1");

        twitchClient = clientBuilder
                .withDefaultEventHandler(SimpleEventHandler.class)
                .withDefaultAuthToken(credential)
                .withEnableHelix(true)
                .withEnableChat(true)
                .build();

        registerEvents();
    }

    /**
     * Registers supported events to the bot.
     */
    private void registerEvents() {
        twitchClient.getEventManager().onEvent(ChannelGoLiveEvent.class, event -> {
            sendMessage("(live event)", event.getChannel().getName(), channelUsers.get(event.getChannel().getName().toLowerCase()));
        });

        twitchClient.getEventManager().onEvent(ChannelChangeTitleEvent.class, event -> {
            sendMessage("(title change event)", event.getChannel().getName(), channelUsers.get(event.getChannel().getName().toLowerCase()));
        });

        twitchClient.getEventManager().onEvent(ChannelChangeGameEvent.class, event -> {
            try {
                wait(1000); //delay for concurrent title and game change
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            sendMessage("(game category change event)", event.getChannel().getName(), channelUsers.get(event.getChannel().getName().toLowerCase()));
        });

        twitchClient.getEventManager().onEvent(ChannelMessageActionEvent.class, event -> {
            if ((event.getMessage().contains("NEW TITLE!") || event.getMessage().contains("NEW GAME!") || event.getMessage().contains("has gone live")) && event.getMessageEvent().getUserName().equalsIgnoreCase("TitleChange_Bot")) {
                List<BotUserModel> users = channelUsers.get(event.getChannel().getName().toLowerCase());
                users.removeIf(u -> !event.getMessage().toLowerCase().contains(u.getName().toLowerCase()));
                sendMessage("(live predict)", event.getChannel().getName(), users);
            }
        });
    }

    /**
     * Requests Discord bot to send the message.
     * @param eventType Identifier of the event for statistical purposes.
     * @param channel Name of the channel that went live.
     */
    private void sendMessage(@NonNull String eventType, @NonNull String channel, @NonNull List<BotUserModel> users) {
        Discord.sendMessage(users, channel + " went live! " + eventType + "\nhttps://www.twitch.tv/" + channel);
    }

    /**
     * Reloads users from file.
     */
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

    /**
     * Adds user to the user map and user file.
     *
     * @param channel Name of the channel that should be connected.
     * @param name Name of the user that should be notified.
     * @param discordID Discord ID of the user.
     */
    public void addUser(String channel, String name, String discordID) {
        if (channelUsers.containsKey(channel)) {
            channelUsers.get(channel).add(new BotUserModel(name, discordID));
        } else {
            channelUsers.put(channel, new ArrayList<>(List.of(new BotUserModel(name, discordID))));
        }
    }

    /**
     * Connects bot to the all required channels.
     */
    public void registerFeatures() {
        twitchClient.getClientHelper().enableStreamEventListener(channelUsers.keySet());
        for (String channel : channelUsers.keySet()) {
            if (!twitchClient.getChat().getChannels().contains(channel)) {
                twitchClient.getChat().joinChannel(channel);
            }
        }
    }

    /**
     * Destroys the bot.
     */
    public void destroy(){
        twitchClient.close();
    }
}
