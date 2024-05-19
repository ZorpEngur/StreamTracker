package com.streamTracker.database.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Class representing user in database.
 */
@Getter @Setter
@RequiredArgsConstructor
public class DatabaseUserModel {

    /**
     * Discord ID of the user used for sending notifications.
     */
    private final long discordId;

    /**
     * Name of the user.
     */
    @NonNull
    private final String name;

    /**
     * Flag if notification should be sent before stream goes live.
     */
    private boolean enableStreamPredict = false;

    /**
     * Flag if stream should be recorded.
     */
    private boolean recordStream = false;
}
