package com.streamTracker.notification;

import com.streamTracker.ApplicationProperties;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
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
    private LocalDateTime lastUsed = LocalDateTime.now();

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
     * Creates the bot that sends the message and shutdowns itself.
     *
     * @param users   List of users that should receive the message.
     * @param message The message to be sent.
     */
    public synchronized void sendMessage(@NonNull List<StreamModel.UserModel> users, @NonNull String message) {
        log.debug("Sending message to users {}, {}", users.stream().map(StreamModel.UserModel::getName).toList(), message);
        this.lastUsed = LocalDateTime.now();
        List<StreamModel.UserModel> filteredUsers = users.stream()
                .filter(u -> u.getLastPing().plus(this.properties.getMessageDelay()).isBefore(LocalDateTime.now()))
                .toList();
        filteredUsers.forEach(u -> u.setLastPing(LocalDateTime.now()));

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
    private void sendMessages(@NonNull List<StreamModel.UserModel> users, @NonNull String message) {
        for (StreamModel.UserModel user : users) {
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
        long timeDiff = this.lastUsed.plus(this.properties.getDiscordShutdownDelay()).toEpochSecond(ZoneOffset.UTC) - LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        while (timeDiff > 0) {
            try {
                Thread.sleep(timeDiff * 1000);
            } catch (Exception e) {
                log.error("Failed to sleep thread for Discord shutdown.");
            }
            timeDiff = this.lastUsed.plus(this.properties.getDiscordShutdownDelay()).toEpochSecond(ZoneOffset.UTC) - LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
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
            this.jdaInstance.shutdown(); this.jdaInstance = null;
        }
    }
}
