package com.streamTracker;

import com.streamTracker.database.twitch.TwitchBotService;
import com.streamTracker.notification.DiscordBot;
import com.streamTracker.notification.StreamRecorder;
import com.streamTracker.notification.TwitchExpandBot;
import com.streamTracker.notification.TwitchLiveBot;
import jakarta.annotation.Nullable;
import lombok.NonNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.time.Clock;

@Configuration
public class SpringConfiguration {

    @Bean @NonNull
    public TwitchLiveBot twitchLiveBot(@NonNull DiscordBot discordBot, @NonNull ApplicationProperties properties,
                                       @NonNull TwitchBotService twitchBotService, @NonNull StreamRecorder streamRecorder) {
        return new TwitchLiveBot(twitchBotService, discordBot, properties, streamRecorder).startBot();
    }

    @Bean @Nullable
    public TwitchExpandBot twitchExpandBot(@NonNull TwitchLiveBot twitchLiveBot, @NonNull ApplicationProperties properties,
                                           @NonNull TwitchBotService twitchBotService) {
        if (properties.getManageChannel() != null) {
            return new TwitchExpandBot(twitchLiveBot, twitchBotService, properties).startBot();
        }
        return null;
    }

    @Bean @NonNull
    public StreamRecorder streamRecorder(@NonNull ApplicationProperties properties) {
        return new StreamRecorder(new File(properties.getFilePath(), "VODs"), properties);
    }

    @Bean @NonNull
    public DiscordBot discordBot(@NonNull ApplicationProperties properties, @NonNull Clock clock) {
        return new DiscordBot(properties, clock);
    }

    @Bean @NonNull
    @ConfigurationProperties("stream.tracker")
    public ApplicationProperties properties() {
        return new ApplicationProperties();
    }
    
    @Bean @NonNull
    public Clock clock() {
        return Clock.systemUTC();
    }
}
