package com.streamTracker.database.properties;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.spockframework.util.Nullable;

import java.time.Duration;

/**
 * Database service for
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PropertiesService {

    /**
     * Only instance of this service.
     */
    @Nullable
    private static PropertiesService singleton;

    /**
     * Database access for properties.
     */
    @NonNull
    private final PropertiesDAO propertiesDAO = new PropertiesDAO();

    /**
     * Method for retrieving instance of this class.
     *
     * @return Singleton of this method.
     */
    @NonNull
    public static synchronized PropertiesService getInstance() {
        if (singleton == null) {
            singleton = new PropertiesService();
        }
        return singleton;
    }

    /**
     * Get file path for where bot will store its data.
     *
     * @return Absolute file path.
     */
    @NonNull
    public String getFilePath() {
        return this.propertiesDAO.getFilePath();
    }

    /**
     * Get name for twitch bot.
     *
     * @return Name.
     */
    @NonNull
    public String getTwitchName() {
        return this.propertiesDAO.getTwitchName();
    }

    /**
     * Get token for twitch bot.
     *
     * @return Token.
     */
    @NonNull
    public String getTwitchToken() {
        return this.propertiesDAO.getTwitchToken();
    }

    /**
     * Get token for Discord bot.
     *
     * @return Token.
     */
    @NonNull
    public String getDiscordToken() {
        return this.propertiesDAO.getDiscordToken();
    }

    /**
     * Get name of channel that manages this bot.
     *
     * @return Name.
     */
    @NonNull
    public String getManageChannel() {
        return this.propertiesDAO.getManageChannel();
    }

    /**
     * Quality of vods.
     *
     * @return StreamLink formated quality of vod.
     */
    @NonNull
    public String getVodResolution() {
        return this.propertiesDAO.getVodResolution();
    }

    /**
     * Delay in minutes before notification from same channel can be sent again.
     *
     * @return Time in minutes.
     */
    public int getMessageDelay() {
        return this.propertiesDAO.getMessageDelay();
    }

    /**
     * Minimum available space in GB to record new vod.
     *
     * @return Size in GB.
     */
    public int getSpaceThreshold() {
        return this.propertiesDAO.getSpaceThreshold();
    }

    /**
     * Duration before Discord bot is shutdown.
     *
     * @return Shutdown duration.
     */
    public Duration getDiscordShutdownDelay() {
        return this.propertiesDAO.getDiscordShutdownDelay();
    }
}
