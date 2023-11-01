package com.zorpengur.notification;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Class representing user.
 */
@Getter
@RequiredArgsConstructor
public class BotUserModel {

    /**
     * Name of the user.
     */
    private final String name;

    /**
     * Discord ID of the user used for sending notifications.
     */
    private final String discordID;

    /**
     * Last time user was pinged.
     */
    @Setter
    private LocalDateTime lastPing = LocalDateTime.MIN;
}
