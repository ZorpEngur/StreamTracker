package com.streamTracker.database.twitch;

import com.streamTracker.database.model.StreamDatabaseModel;
import com.streamTracker.notification.StreamModel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Database service for twitch bots.
 */
@RequiredArgsConstructor
public class TwitchBotService {

    /**
     * Database access for twitch bot operations.
     */
    @NonNull
    private final TwitchBotDAO twitchBotDAO;

    /**
     * Loads all streamers from database.
     *
     * @return List of all streamers.
     */
    @NonNull
    public List<StreamModel> getStreamerModels() {
        return this.twitchBotDAO.getStreamModels().stream()
                .collect(Collectors.groupingBy(TwitchUserRelModel::getStreamId))
                .values().stream()
                .map(l -> new StreamModel(l.get(0).getStreamName(),
                        l.stream().anyMatch(TwitchUserRelModel::isRecordStream),
                        l.stream().map(u -> new StreamModel.UserModel(u.getUserId(), u.isStreamPrediction(), u.getNotificationPlatform())).toList()))
                .toList();
    }

    /**
     * Saves new user to database and subscribes to specified stream event.
     *
     * @param userRegistration Data about user and stream event.
     */
    public void addUser(@NonNull TwitchUserRelModel userRegistration) {
        StreamDatabaseModel stream = this.twitchBotDAO.getStream(userRegistration.getStreamName());
        if (stream == null) {
            this.twitchBotDAO.insertStream(userRegistration);
        } else {
            userRegistration.setStreamId(stream.getId());
        }
        if (!this.twitchBotDAO.relExist(userRegistration)) {
            this.twitchBotDAO.insertRel(userRegistration);
        }
    }
}
