package com.zorpengur.notification;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Bot that send the message on discord.
 */
@Slf4j
public class Discord extends ListenerAdapter {

    /**
     * The bot JDA.
     */
    private static JDA jda;

    /**
     * Creates the bot that sends the message and shutdowns itself.
     *
     * @param users List of users that should receive the message.
     * @param message The message to be sent.
     */
    public static void sendMessage(@NonNull List<BotUserModel> users, @NonNull String message){
        log.debug("Sending message to users {}, {}", users.stream().map(BotUserModel::getName), message);
        users.removeIf(u -> u.getLastPing().isAfter(LocalDateTime.now().minusMinutes(5)));
        users.forEach(u -> u.setLastPing(LocalDateTime.now()));
        Discord bot = new Discord();
        try {
            GlobalScreen.registerNativeHook();
        }
        catch (NativeHookException ex) {
            log.error("There was a problem registering the native hook.", ex);
            System.exit(1);
        }

        try {
            if (jda == null) {
                jda = JDABuilder.createDefault("NzY0ODY5MDQxNjY3MTc4NTE3.GzuyDb.BbJd8wnRqN-fJ68NO26VNfktwlQboNEmfhiHKQ") // The token of the account that is logging in.
                        .addEventListeners(bot)   // An instance of a class that will handle events.
                        .enableCache(CacheFlag.VOICE_STATE)
                        .setStatus(OnlineStatus.OFFLINE)
                        .build();
                jda.awaitReady(); // Blocking guarantees that JDA will be completely loaded.
            }
            System.out.println("Done!");
        }
        catch (Exception e)
        {
            log.error("Bot building error", e);
        }

        sendMessages(users, message);
    }


    private static void sendMessages(@NonNull List<BotUserModel> users, @NonNull String message) {
        for (BotUserModel user : users) {
            jda.openPrivateChannelById(user.getDiscordID()).queue((privateChannel -> privateChannel.sendMessage(message).queue()));
        }
    }

    public static void destroy() {
        jda.shutdown();
    }
}
