package com.streamTracker.database.properties;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.spockframework.util.Nullable;

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
        return propertiesDAO.getFilePath();
    }

    /**
     * Get name for twitch bot.
     *
     * @return Name.
     */
    @NonNull
    public String getTwitchName() {
        return propertiesDAO.getTwitchName();
    }

    /**
     * Get token for twitch bot.
     *
     * @return Token.
     */
    @NonNull
    public String getTwitchToken() {
        return propertiesDAO.getTwitchToken();
    }

    /**
     * Get token for Discord bot.
     *
     * @return Token.
     */
    @NonNull
    public String getDiscordToken() {
        return propertiesDAO.getDiscordToken();
    }

    /**
     * Get name of channel that manages this bot.
     *
     * @return Name.
     */
    @NonNull
    public String getManageChannel() {
        return propertiesDAO.getManageChannel();
    }
}
