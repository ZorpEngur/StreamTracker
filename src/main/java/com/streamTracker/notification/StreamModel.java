package com.streamTracker.notification;

import com.streamTracker.database.model.NotificationPlatform;
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
@RequiredArgsConstructor
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

    /**
     * Class representing user.
     */
    @Getter
    @RequiredArgsConstructor
    public static class UserModel {

        /**
         * ID of the user
         */
        private final int id;

        /**
         * Flag if notification should be sent before stream goes live.
         */
        private final boolean enableStreamPredict;

        /**
         * Last time user was pinged.
         */
        @Setter
        private LocalDateTime lastPing = LocalDateTime.MIN;

        /**
         * Platform where the user will be notified.
         */
        @NonNull
        private final NotificationPlatform notificationPlatform;
    }
}
