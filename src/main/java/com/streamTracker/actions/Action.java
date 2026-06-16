package com.streamTracker.actions;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NonNull;

import java.time.Clock;
import java.time.LocalDateTime;

/**
 * Action that was performed by the application.
 */
@Getter
public class Action {

    /**
     * Version of the action.
     */
    @JsonProperty(required = true)
    private final int version;

    /**
     * Code of the action.
     */
    @NonNull
    @JsonProperty(required = true)
    private final String code;

    /**
     * If the action shouldn't be performed by fallback server.
     */
    @JsonProperty(required = true)
    private final boolean filterDuplicit;

    /**
     * Specific body of the action.
     */
    @NonNull
    @JsonProperty(required = true)
    private final String body;

    /**
     * Time when the action was performed.
     */
    @NonNull
    @JsonProperty(required = true)
    private final LocalDateTime date;

    private Action(int version, @NonNull String code, boolean filterDuplicit, @NonNull String body, @NonNull LocalDateTime date) {
        this.version = version;
        this.code = code;
        this.filterDuplicit = filterDuplicit;
        this.body = body;
        this.date = date;
    }

    /**
     * Action when the notification was sent to user.
     *
     * @param userName Name of the user that got the notification.
     * @param message  Message that was sent to the user.
     * @param clock    Current clock of the application.
     * @return The action.
     */
    @NonNull
    public static Action notification(@NonNull String userName, @NonNull String message, @NonNull Clock clock) {
        return new Action(
                1,
                "NOTIFICATION",
                true,
                "Message " + message + " sent to user " + userName,
                LocalDateTime.now(clock)
        );
    }

    /**
     * Action when user triggers command in chat.
     *
     * @param channelName Name of the channel where was the command called.
     * @param response    Response created for the command.
     * @param clock       Current clock of the application.
     * @return The action.
     */
    @NonNull
    public static Action commandResponse(@NonNull String channelName, @NonNull String response, @NonNull Clock clock) {
        return new Action(
                1,
                "COMMAND_RESPONSE",
                true,
                "In channel " + channelName + " response was sent " + response,
                LocalDateTime.now(clock)
        );
    }
}
