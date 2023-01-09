package org.example;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

public class Discord extends ListenerAdapter {

    private static JDA jda;
    private static String DM;
    private static List<BotUserModel> USERS;

    public static void sendMessage(List<BotUserModel> users, String message){
        users.removeIf(u -> u.getLastPing().isAfter(LocalDateTime.now().minusMinutes(10)));
        users.forEach(u -> u.setLastPing(LocalDateTime.now()));
        USERS = users;
        DM = message;
        Discord bot = new Discord();
        try {
            GlobalScreen.registerNativeHook();
        }
        catch (NativeHookException ex) {
            //log.error("There was a problem registering the native hook.", ex);
            System.exit(1);
        }

        try
        {
            jda = JDABuilder.createDefault("NzY0ODY5MDQxNjY3MTc4NTE3.GzuyDb.BbJd8wnRqN-fJ68NO26VNfktwlQboNEmfhiHKQ") // The token of the account that is logging in.
                    .addEventListeners(bot)   // An instance of a class that will handle events.
                    .enableCache(CacheFlag.VOICE_STATE)
                    .setStatus(OnlineStatus.OFFLINE)
                    .build();
            jda.awaitReady(); // Blocking guarantees that JDA will be completely loaded.
            System.out.println("Done!");
        }
        catch (Exception e)
        {
            //log.error("Bot building error", e);
        }
    }

    @Override
    synchronized public void onReady(@NotNull ReadyEvent event) {
        for (BotUserModel user : USERS) {
            jda.openPrivateChannelById(user.getDiscordID()).queue((privateChannel -> privateChannel.sendMessage(DM).queue()));
            onShutdown(new ShutdownEvent(jda, OffsetDateTime.now(), 1));
        }
    }

    @Override
    public void onShutdown(@NotNull ShutdownEvent event) {
        super.onShutdown(event);
    }
}
