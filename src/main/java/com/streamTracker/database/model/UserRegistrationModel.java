package com.streamTracker.database.twitch;

import com.streamTracker.database.model.NotificationPlatform;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.Nullable;

/**
 * Model for data passed by user when making registration with bot.
 */
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class TwitchUserRelModel {

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
     * ID of the user.
     */
    private int userId;

    /**
     * If stream should be recoded.
     */
    private boolean recordStream;

    /**
     * If notification should be sent ahead of stream going live.
     */
    private boolean streamPrediction;

    /**
     * Identifier of the notification platform.
     */
    @NonNull
    private NotificationPlatform notificationPlatform;
}
