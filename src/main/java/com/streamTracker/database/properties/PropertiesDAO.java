package com.streamTracker.database.properties;

import com.streamTracker.database.DatabaseReader;
import lombok.NoArgsConstructor;
import lombok.NonNull;

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
}
