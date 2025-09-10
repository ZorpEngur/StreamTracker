package com.streamTracker;

import com.streamTracker.api.RestConfiguration;
import com.streamTracker.database.DatabaseConfiguration;
import com.streamTracker.database.twitch.TwitchBotService;
import com.streamTracker.database.user.UserService;
import com.streamTracker.notification.CommandManager;
import com.streamTracker.notification.DiscordBot;
import com.streamTracker.notification.TwitchCommandBot;
import com.streamTracker.notification.TwitchLiveBot;
import com.streamTracker.recorder.ChatRecorder;
import com.streamTracker.recorder.FileController;
import com.streamTracker.recorder.StreamRecorder;
import jakarta.annotation.Nullable;
import lombok.NonNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.time.Clock;

@Configuration
@Import({DatabaseConfiguration.class, RestConfiguration.class})
public class SpringConfiguration {

    @Bean
    @NonNull
    public TwitchLiveBot twitchLiveBot(@NonNull DiscordBot discordBot, @NonNull ApplicationProperties properties,
                                       @NonNull TwitchBotService twitchBotService, @NonNull StreamRecorder streamRecorder,
                                       @NonNull ChatRecorder chatRecorder) {
        return new TwitchLiveBot(twitchBotService, discordBot, properties, streamRecorder, chatRecorder).startBot();
    }

    @Bean
    @Nullable
    public TwitchCommandBot twitchCommandBot(@NonNull ApplicationProperties properties, @NonNull CommandManager commandManager) {
        if (properties.getManageChannel() != null) {
            return new TwitchCommandBot(properties, commandManager).startBot();
        }
        return null;
    }

    @Bean
    @NonNull
    public CommandManager commandManager(@NonNull TwitchBotService twitchBotService, @NonNull UserService userService) {
        return new CommandManager(twitchBotService, userService);
    }

    @Bean
    @NonNull
    public StreamRecorder streamRecorder(@NonNull ApplicationProperties properties, @NonNull FileController fIleController) {
        return new StreamRecorder(fIleController, properties);
    }

    @Bean
    @NonNull
    public FileController fIleController(@NonNull ApplicationProperties properties) {
        return new FileController(properties);
    }

    @Bean
    @NonNull
    public DiscordBot discordBot(@NonNull ApplicationProperties properties, @NonNull Clock clock,
                                 @NonNull UserService userService) {
        return new DiscordBot(properties, clock, userService);
    }

    @Bean
    @NonNull
    public ChatRecorder chatRecorder(@NonNull FileController fileController, @NonNull ApplicationProperties properties) {
        return new ChatRecorder(fileController, properties);
    }

    @Bean
    @NonNull
    @ConfigurationProperties("stream.tracker")
    public ApplicationProperties properties() {
        return new ApplicationProperties();
    }

    @Bean
    @NonNull
    public Clock clock() {
        return Clock.systemUTC();
    }
}
