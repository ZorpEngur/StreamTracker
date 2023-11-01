package com.streamTracker.notification;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.channel.ChannelMessageActionEvent;
import com.github.twitch4j.events.ChannelChangeGameEvent;
import com.github.twitch4j.events.ChannelChangeTitleEvent;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import com.streamTracker.database.properties.PropertiesService;
import com.streamTracker.database.twitch.TwitchBotService;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.spockframework.util.Nullable;

import java.util.List;

/**
 * Bot that sends notifications.
 */
@Slf4j
@NoArgsConstructor
public class TwitchLiveBot {

    /**
     * List of registered streams.
     */
    @Nullable
    private List<StreamModel> streamModels;

    /**
     * Client of the bot.
     */
    @Nullable
    private TwitchClient twitchClient;

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

    /**
     * Starts twitch bot and register event handlers.
     *
     * @see TwitchLiveBot#registerFeatures() Needs to be called to join bot to channels.
     */
    public void startBot() {

        TwitchClientBuilder clientBuilder = TwitchClientBuilder.builder();

        OAuth2Credential credential = new OAuth2Credential(this.propertiesService.getTwitchName(), this.propertiesService.getTwitchToken());

        twitchClient = clientBuilder
            .withDefaultEventHandler(SimpleEventHandler.class)
            .withDefaultAuthToken(credential)
            .withEnableHelix(true)
            .withEnableChat(true)
            .build();

        loadUsers();
    }

    /**
     * Loads all users for this bot.
     */
    public void loadUsers() {
        streamModels = this.twitchBotService.getStreamerModels();
        streamModels.forEach(m -> log.debug("Loaded channel: {} with record stream: {} and users: {}", m.getStreamName(), m.isRecordStream(), m.getUsers().stream().map(StreamModel.UserModel::getName).toList()));
        registerEvents();
        registerFeatures();
    }

    /**
     * Registers supported events to the bot.
     */
    private void registerEvents() {
        twitchClient.getEventManager().onEvent(ChannelGoLiveEvent.class, event -> {
            StreamModel streamModel = getStreamModel(event.getChannel().getName());
            sendMessage("(live event)", streamModel.getStreamName(), streamModel.getUsers());
            if (streamModel.isRecordStream()) {
                StreamRecorder.record(streamModel.getStreamName());
            }
        });

        twitchClient.getEventManager().onEvent(ChannelChangeTitleEvent.class, event ->
            sendMessage("(title change event)", event.getChannel().getName(), getStreamModel(event.getChannel().getName()).getUsers()));

        twitchClient.getEventManager().onEvent(ChannelChangeGameEvent.class, event -> {
            sendMessage("(game category change event)", event.getChannel().getName(), getStreamModel(event.getChannel().getName()).getUsers());
        });

        twitchClient.getEventManager().onEvent(ChannelMessageActionEvent.class, event -> {
            if ((event.getMessage().contains("NEW TITLE!") || event.getMessage().contains("NEW GAME!") || event.getMessage().contains("has gone live")) && event.getMessageEvent().getUserName().equalsIgnoreCase("TitleChange_Bot")) {
                List<StreamModel.UserModel> users = getStreamModel(event.getChannel().getName()).getUsers()
                    .stream()
                    .filter(StreamModel.UserModel::isEnableStreamPredict)
                    .filter(u -> event.getMessage().toLowerCase().contains(u.getName().toLowerCase()))
                    .toList();
                sendMessage("(live predict)", event.getChannel().getName(), users);
            }
        });
    }

    /**
     * Requests Discord bot to send the message.
     *
     * @param eventType Identifier of the event for statistical purposes.
     * @param channel   Name of the channel that went live.
     */
    private void sendMessage(@NonNull String eventType, @NonNull String channel, @NonNull List<StreamModel.UserModel> users) {
        log.trace("Send message called with event {} for channel {}, notifying users {}", eventType, channel, users.stream().map(StreamModel.UserModel::getName).toList());
        DiscordBot.sendMessage(users, channel + " went live! " + eventType + "\nhttps://www.twitch.tv/" + channel);
    }

    /**
     * Finds stream model matching the name. This is just basic lookup mechanism.
     * Should always look for existing model, otherwise exception will be thrown.
     *
     * @param streamName Name of desired model.
     * @return Stream model or null.
     */
    @NonNull
    private StreamModel getStreamModel(@NonNull String streamName) {
        for (StreamModel stream : streamModels) {
            if (stream.getStreamName().equalsIgnoreCase(streamName)) {
                return stream;
            }
        }
        throw new RuntimeException("Could not find stream " + streamName + " in stream models.");
    }

    /**
     * Connects bot to the all required channels.
     */
    private void registerFeatures() {
        List<String> streams = streamModels.stream().map(StreamModel::getStreamName).toList();
        twitchClient.getClientHelper().enableStreamEventListener(streams);
        for (String stream : streams) {
            if (!twitchClient.getChat().getChannels().contains(stream)) {
                twitchClient.getChat().joinChannel(stream);
            }
        }
    }

    /**
     * Destroys the bot.
     */
    public void destroy() {
        twitchClient.close();
        DiscordBot.destroy();
    }
}
