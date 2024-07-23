package com.streamTracker.database.properties;

import com.streamTracker.database.DatabaseReader;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.Duration;

@NoArgsConstructor
class PropertiesDAO extends DatabaseReader<IPropertiesMapper> {

    /**
     * Get file path for where bot will store its data.
     *
     * @return Absolute file path.
     */
    @NonNull
    protected String getFilePath() {
        return get(IPropertiesMapper::getFilePath);
    }

    /**
     * Get name for twitch bot.
     *
     * @return Name.
     */
    @NonNull
    protected String getTwitchName() {
        return get(IPropertiesMapper::getTwitchName);
    }

    /**
     * Get token for twitch bot.
     *
     * @return Token.
     */
    @NonNull
    protected String getTwitchToken() {
        return get(IPropertiesMapper::getTwitchToken);
    }

    /**
     * Get token for Discord bot.
     *
     * @return Token.
     */
    @NonNull
    protected String getDiscordToken() {
        return get(IPropertiesMapper::getDiscordToken);
    }

    /**
     * Get name of channel that manages this bot.
     *
     * @return Name.
     */
    @NonNull
    protected String getManageChannel() {
        return get(IPropertiesMapper::getManageChannel);
    }

    /**
     * Quality of the vods.
     *
     * @return StreamLink formated quality of vod.
     */
    @NonNull
    protected String getVodResolution() {
        return get(IPropertiesMapper::getVodResolution);
    }

    /**
     * Delay in minutes before notification from same channel can be sent again.
     *
     * @return Time in minutes.
     */
    protected int getMessageDelay() {
        return Integer.parseInt(get(IPropertiesMapper::getMessageDelay));
    }

    /**
     * Minimum available space in GB to record new vod.
     *
     * @return Size in GB.
     */
    protected int getSpaceThreshold() {
        return Integer.parseInt(get(IPropertiesMapper::getSpaceThreshold));
    }

    /**
     * Duration before Discord bot is shutdown.
     *
     * @return Shutdown duration.
     */
    protected Duration getDiscordShutdownDelay() {
        return Duration.parse(get(IPropertiesMapper::getDiscordShutdownDelay));
    }
}
