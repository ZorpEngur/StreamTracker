package com.streamTracker.notification;

import com.streamTracker.ApplicationProperties;
import com.streamTracker.database.model.NotificationPlatform;
import com.streamTracker.database.model.UserDatabaseModel;
import com.streamTracker.database.user.UserService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.jetbrains.annotations.Nullable;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Bot that send the message on discord.
 */
@Slf4j
@RequiredArgsConstructor
public class DiscordBot extends ListenerAdapter {

    /**
     * The bot JDA instance.
     */
    @Nullable
    private JDA jdaInstance;

    /**
     * Last time bot was used. Based on this time bot will be shut down after {@link ApplicationProperties#getDiscordShutdownDelay()}.
     */
    @NonNull
    private LocalDateTime lastUsed = LocalDateTime.MIN;

    /**
     * Flag if method to shut down bot was already called by different thread.
     */
    @NonNull
    private final AtomicBoolean destroyLock = new AtomicBoolean(false);

    /**
     * Properties of the application.
     */
    @NonNull
    private final ApplicationProperties properties;

    /**
     * System clock.
     */
    @NonNull
    private final Clock clock;

    /**
     * Service for getting users.
     */
    @NonNull
    private final UserService userService;

    /**
     * Creates the bot that sends the message and shutdowns itself.
     *
     * @param users   List of users that should receive the message.
     * @param message The message to be sent.
     */
    public synchronized void sendMessage(@NonNull List<StreamModel.UserModel> users, @NonNull String message) {
        this.lastUsed = LocalDateTime.now(this.clock);
        List<DiscordUser> filteredUsers = users.stream()
                .filter(u -> u.getNotificationPlatform().equals(NotificationPlatform.DISCORD))
                .filter(u -> u.getLastPing().plus(this.properties.getMessageDelay()).isBefore(LocalDateTime.now(this.clock)))
                .map(u -> {
                    UserDatabaseModel user = this.userService.getUser(u.getId());
                    if (user != null && user.getDiscordId() != null)
                        return new DiscordUser(u, user.getDiscordId(), user.getName());
                    return null;
                })
                .filter(Objects::nonNull)
                .map(u -> u.setLastPing(LocalDateTime.now(this.clock)))
                .toList();

        if (filteredUsers.isEmpty()) {
            return;
        }

        try {
            if (this.jdaInstance == null || !this.jdaInstance.getStatus().equals(JDA.Status.CONNECTED)) {
                log.debug("Creating new Discord JDA instance.");
                if (this.jdaInstance != null) {
                    this.jdaInstance.shutdownNow();
                }
                this.jdaInstance = JDABuilder.createDefault(this.properties.getDiscordToken())
                        .enableCache(CacheFlag.VOICE_STATE)
                        .setStatus(OnlineStatus.OFFLINE)
                        .build();
                this.jdaInstance.awaitReady();
            }
        } catch (Exception e) {
            log.error("Bot building error", e);
            return;
        }

        sendMessages(filteredUsers, message);
    }

    /**
     * Sends message and ques shutdown of the bot.
     *
     * @param users   List of users whom the message will be sent.
     * @param message The message.
     */
    private void sendMessages(@NonNull List<DiscordUser> users, @NonNull String message) {
        log.debug("Sending message to users {}, {}", users.stream().map(DiscordUser::getName).toList(), message);
        for (DiscordUser user : users) {
            assert this.jdaInstance != null : "JDA instance should be set at this point.";
            this.jdaInstance.openPrivateChannelById(user.getDiscordId()).queue((privateChannel -> privateChannel.sendMessage(message).queue()));
        }
        if (this.destroyLock.compareAndSet(false, true)) {
            new Thread(this::timedShutDown).start();
        }
    }

    /**
     * Method that will shut down the bot after {@link ApplicationProperties#getDiscordShutdownDelay()} from {@link #lastUsed}.
     * NOTE: Lifetime of the bot can be extended if {@link #lastUsed} is updated.
     */
    private void timedShutDown() {
        if (this.jdaInstance == null) {
            return;
        }
        long timeDiff = this.lastUsed.plus(this.properties.getDiscordShutdownDelay()).toEpochSecond(ZoneOffset.UTC) - LocalDateTime.now(this.clock).toEpochSecond(ZoneOffset.UTC);
        while (timeDiff > 0) {
            try {
                Thread.sleep(timeDiff * 1000);
            } catch (Exception e) {
                log.error("Failed to sleep thread for Discord shutdown.");
            }
            timeDiff = this.lastUsed.plus(this.properties.getDiscordShutdownDelay()).toEpochSecond(ZoneOffset.UTC) - LocalDateTime.now(this.clock).toEpochSecond(ZoneOffset.UTC);
        }
        if (this.jdaInstance != null) {
            JDA pointer = this.jdaInstance;
            this.jdaInstance = null;
            pointer.shutdown();
            log.debug("Discord bot shut down.");
        }
        this.destroyLock.set(false);
    }

    /**
     * Destroys the bot.
     */
    public void destroy() {
        if (this.jdaInstance != null) {
            this.jdaInstance.shutdown();
            this.jdaInstance = null;
        }
    }

    /**
     * Discord user data.
     */
    @AllArgsConstructor
    private static class DiscordUser {

        /**
         * User data from the stream.
         */
        @NonNull
        private StreamModel.UserModel user;

        /**
         * Discord ID of the user.
         */
        @Getter
        private long discordId;

        /**
         * Name of the user.
         */
        @NonNull
        @Getter
        private String name;

        /**
         * Sets last pinged time for the user.
         *
         * @param time Time when the user was pinged.
         * @return The user.
         */
        @NonNull
        public DiscordUser setLastPing(@NonNull LocalDateTime time) {
            this.user.setLastPing(time);
            return this;
        }

    }
}
