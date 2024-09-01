package com.streamTracker.database.twitch;

import com.streamTracker.database.model.DatabaseStreamModel;
import com.streamTracker.notification.StreamModel;
import com.streamTracker.database.model.UserRegistrationModel;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Database service for twitch bots.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TwitchBotService {

    /**
     * Only instance of this service.
     */
    @Nullable
    private static TwitchBotService singleton;

    /**
     * Database access for twitch bot operations.
     */
    @NonNull
    private final TwitchBotDAO twitchBotDAO = new TwitchBotDAO();

    /**
     * Method for retrieving instance of this class.
     *
     * @return Singleton of this method.
     */
    @NonNull
    public static synchronized TwitchBotService getInstance() {
        if (singleton == null) {
            singleton = new TwitchBotService();
        }
        return singleton;
    }

    /**
     * Loads all streamers from database.
     *
     * @return List of all streamers.
     */
    @NonNull
    public List<StreamModel> getStreamerModels() {
        List<StreamModel> result = new ArrayList<>();
        for (DatabaseStreamModel stream : this.twitchBotDAO.getStreams()) {
            result.add(new StreamModel(stream.getName(), this.twitchBotDAO.getChannelUsers(stream.getId())));
        }
        return result;
    }

    /**
     * Saves new user to database and subscribes to specified stream event.
     *
     * @param userRegistration Data about user and stream event.
     */
    public void addUser(@NonNull UserRegistrationModel userRegistration) {
        if (!this.twitchBotDAO.isUser(userRegistration.getDiscordId())) {
            this.twitchBotDAO.insertUser(userRegistration);
        }
        DatabaseStreamModel stream = this.twitchBotDAO.getStream(userRegistration.getStreamName());
        if (stream == null) {
            this.twitchBotDAO.insertStream(userRegistration);
        } else {
            userRegistration.setStreamId(stream.getId());
        }
        if (!this.twitchBotDAO.relExist(userRegistration)){
            this.twitchBotDAO.insertRel(userRegistration);
        }
    }
}
