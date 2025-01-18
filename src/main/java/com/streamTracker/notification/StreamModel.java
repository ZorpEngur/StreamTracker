package com.streamTracker.notification;

import com.streamTracker.database.model.DatabaseUserModel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Model of stream containing data about stream and all users registered to it.
 */
@Getter
public class StreamModel {

    /**
     * Name of the stream.
     */
    @NonNull
    private final String streamName;

    /**
     * Flag if stream should be recorded.
     */
    private final boolean recordStream;

    /**
     * Users registered in this stream.
     */
    @NonNull
    private final List<UserModel> users;

    public StreamModel(@NonNull String streamName, @NonNull List<DatabaseUserModel> users) {
        this.streamName = streamName;
        this.recordStream = users.stream().anyMatch(DatabaseUserModel::isRecordStream);
        this.users = users.stream().map(UserModel::new).toList();
    }

    /**
     * Class representing user.
     */
    @Getter
    @RequiredArgsConstructor
    public static class UserModel {

        public UserModel(DatabaseUserModel user) {
            this(user.getName(), user.getDiscordId(), user.isEnableStreamPredict());
        }

        /**
         * Name of the user.
         */
        @NonNull
        private final String name;

        /**
         * Discord ID of the user used for sending notifications.
         */
        private final long discordId;

        /**
         * Flag if notification should be sent before stream goes live.
         */
        private final boolean enableStreamPredict;

        /**
         * Last time user was pinged.
         */
        @Setter
        private LocalDateTime lastPing = LocalDateTime.MIN;
    }
}
