package com.streamTracker.database.model;

import jakarta.annotation.Nullable;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Class representing user in database.
 */
@Getter
@Setter
@Builder
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public class UserDatabaseModel {

    /**
     * ID of the user.
     */
    @Nullable
    private final Integer id;

    /**
     * Discord ID of the user used for sending notifications.
     */
    @Nullable
    private final Long discordId;

    /**
     * Name of the user.
     */
    @NonNull
    private final String name;

    /**
     * Twitch ID of the user.
     */
    @Nullable
    private final Long twitchId;
}
