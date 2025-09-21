package com.streamTracker;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import java.time.Duration;

@Setter
@Getter
@NoArgsConstructor
public class ApplicationProperties {

    /**
     * File path for where bot will store its data.
     */
    @NonNull
    private String filePath;

    /**
     * Name for twitch bot.
     */
    @NonNull
    private String twitchName;

    /**
     * Token for twitch bot.
     */
    @NonNull
    private String twitchToken;

    /**
     * Token for Discord bot.
     */
    @NonNull
    private String discordToken;

    /**
     * Name of channel that manages this bot.
     */
    @Nullable
    private String manageChannel;

    /**
     * Quality of vods.
     */
    @NonNull
    private String vodResolution;

    /**
     * Delay in minutes before notification from same channel can be sent again.
     */
    @NonNull
    private Duration messageDelay;

    /**
     * Minimum available space in GB to record new vod.
     */
    private int spaceThreshold;

    /**
     * Duration before Discord bot is shutdown.
     */
    @NonNull
    private Duration discordShutdownDelay;

    /**
     * Size of the message que before recorder saves it on disk.
     */
    private int chatMessageQueueSize;

    /**
     * Duration for which the stream recording will be retried.
     */
    @NonNull
    private Duration streamRecordingRetry;
}
