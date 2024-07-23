package com.streamTracker.notification;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.streamTracker.database.properties.PropertiesService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.spockframework.util.Nullable;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Bot that send the message on discord.
 */
@Slf4j
public class DiscordBot extends ListenerAdapter {

    /**
     * The bot JDA instance.
     */
    @Nullable
    private static JDA JDA_INSTANCE;

    /**
     * Last time bot was used. Based on this time bot will be shut down after {@link #SHUTDOWN_DELAY}.
     */
    @NonNull
    private static LocalDateTime LAST_USED = LocalDateTime.now();

    /**
     * Delay before bot will be shut down.
     */
    @NonNull
    private static final TemporalAmount SHUTDOWN_DELAY = PropertiesService.getInstance().getDiscordShutdownDelay();

    /**
     * Flag if method to shut down bot was already called by different thread.
     */
    @NonNull
    private static final AtomicBoolean DESTROY_LOCK = new AtomicBoolean(false);

    /**
     * Delay before another message can be sent to user.
     */
    private static final int REPEATED_MESSAGE_DELAY = PropertiesService.getInstance().getMessageDelay();

    static {
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException ex) {
            log.error("There was a problem registering the native hook.", ex);
            System.exit(1);
        }
    }

    /**
     * Creates the bot that sends the message and shutdowns itself.
     *
     * @param users   List of users that should receive the message.
     * @param message The message to be sent.
     */
    public synchronized static void sendMessage(@NonNull List<StreamModel.UserModel> users, @NonNull String message) {
        log.debug("Sending message to users {}, {}", users.stream().map(StreamModel.UserModel::getName).toList(), message);
        LAST_USED = LocalDateTime.now();
        List<StreamModel.UserModel> filteredUsers = users.stream()
                .filter(u -> u.getLastPing().plusMinutes(REPEATED_MESSAGE_DELAY).isBefore(LocalDateTime.now()))
                .toList();
        filteredUsers.forEach(u -> u.setLastPing(LocalDateTime.now()));

        try {
            if (JDA_INSTANCE == null || !JDA_INSTANCE.getStatus().equals(JDA.Status.CONNECTED)) {
                log.debug("Creating new Discord JDA instance.");
                if (JDA_INSTANCE != null) {
                    JDA_INSTANCE.shutdownNow();
                }
                JDA_INSTANCE = JDABuilder.createDefault(PropertiesService.getInstance().getDiscordToken())
                        .enableCache(CacheFlag.VOICE_STATE)
                        .setStatus(OnlineStatus.OFFLINE)
                        .build();
                JDA_INSTANCE.awaitReady();
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
    private static void sendMessages(@NonNull List<StreamModel.UserModel> users, @NonNull String message) {
        for (StreamModel.UserModel user : users) {
            JDA_INSTANCE.openPrivateChannelById(user.getDiscordId()).queue((privateChannel -> privateChannel.sendMessage(message).queue()));
        }
        if (DESTROY_LOCK.compareAndSet(false, true)) {
            new Thread(DiscordBot::timedShutDown).start();
        }
    }

    /**
     * Method that will shut down the bot after {@link #SHUTDOWN_DELAY} from {@link #LAST_USED}.
     * NOTE: Lifetime of the bot can be extended if {@link #LAST_USED} is updated.
     */
    private static void timedShutDown() {
        if (JDA_INSTANCE == null) {
            return;
        }
        long timeDiff = LAST_USED.plus(SHUTDOWN_DELAY).toEpochSecond(ZoneOffset.UTC) - LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        while (timeDiff > 0) {
            try {
                Thread.sleep(timeDiff * 1000);
            } catch (Exception e) {
                log.error("Failed to sleep thread for Discord shutdown.");
            }
            timeDiff = LAST_USED.plus(SHUTDOWN_DELAY).toEpochSecond(ZoneOffset.UTC) - LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        }
        if (JDA_INSTANCE != null) {
            JDA pointer = JDA_INSTANCE;
            JDA_INSTANCE = null;
            pointer.shutdown();
            log.debug("Discord bot shut down.");
        }
        DESTROY_LOCK.set(false);
    }

    /**
     * Destroys the bot.
     */
    public static void destroy() {
        if (JDA_INSTANCE != null) {
            JDA_INSTANCE.shutdown();
            JDA_INSTANCE = null;
        }
    }
}
