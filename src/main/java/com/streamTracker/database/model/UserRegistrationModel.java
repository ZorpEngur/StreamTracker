package com.streamTracker.database.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

/**
 * Model for data passed by user when making registration with bot.
 */
@Builder @Getter
public class UserRegistrationModel {

    /**
     * Database id of the stream.
     */
    @Setter
    @Nullable
    private Integer streamId;

    /**
     * Name of the stream.
     */
    @NonNull
    private String streamName;

    /**
     * Name of the user.
     */
    @NonNull
    private String userName;

    /**
     * Discord ID of the user used for sending notifications.
     */
    private long discordId;

    /**
     * If stream should be recoded.
     */
    private boolean recordStream;

    /**
     * If notification should be sent ahead of stream going live.
     */
    private boolean streamPrediction;

    @Override
    public String toString() {
        return "Stream ID: " + streamId + ", stream name: " + streamName + ", user name: " + userName + ", discord ID: "
            + discordId + ", record stream: " + recordStream + ", stream prediction: " + streamPrediction;
    }
}
