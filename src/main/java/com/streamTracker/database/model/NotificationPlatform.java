package com.streamTracker.database.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

/**
 * * Enum representing all platforms available for sending notifications.
 */
@AllArgsConstructor
@Getter
public enum NotificationPlatform {
    DISCORD(1);

    /**
     * Database ID of the platform.
     */
    private final int id;

    /**
     * Retrieves notification platform by its ID.
     *
     * @param id ID of the notification platform.
     * @return The notification platform or {@code null} if the platform was not found.
     */
    @Nullable
    public static NotificationPlatform fromId(@Nullable Integer id) {
        for (NotificationPlatform platform : values()) {
            if (id != null && platform.getId() == id) {
                return platform;
            }
        }
        return null;
    }

    /**
     * Retrieves notification platform from its name.
     *
     * @param name Name of the platform.
     * @return The notification platform or {@code null} if the platform was not found.
     */
    @Nullable
    public static NotificationPlatform fromName(@NonNull String name) {
        return switch (name.trim().toLowerCase()) {
            case "discord" -> NotificationPlatform.DISCORD;
            default -> null;
        };
    }
}
