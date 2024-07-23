package com.streamTracker.database.properties;

import org.apache.ibatis.annotations.Select;

interface IPropertiesMapper {

    @Select("SELECT property_value FROM stream_tracker.properties WHERE property_name = 'BOT_FILE'")
    String getFilePath();

    @Select("SELECT property_value FROM stream_tracker.properties WHERE property_name = 'TWITCH_NAME'")
    String getTwitchName();

    @Select("SELECT property_value FROM stream_tracker.properties WHERE property_name = 'TWITCH_TOKEN'")
    String getTwitchToken();

    @Select("SELECT property_value FROM stream_tracker.properties WHERE property_name = 'DISCORD_TOKEN'")
    String getDiscordToken();

    @Select("SELECT property_value FROM stream_tracker.properties WHERE property_name = 'MANAGE_CHANNEL_NAME'")
    String getManageChannel();

    @Select("SELECT property_value FROM stream_tracker.properties WHERE property_name = 'VOD_RESOLUTION'")
    String getVodResolution();

    @Select("SELECT property_value FROM stream_tracker.properties WHERE property_name = 'MESSAGE_DELAY'")
    String getMessageDelay();

    @Select("SELECT property_value FROM stream_tracker.properties WHERE property_name = 'SPACE_THRESHOLD'")
    String getSpaceThreshold();

    @Select("SELECT property_value FROM stream_tracker.properties WHERE property_name = 'DISCORD_SHUTDOWN_DELAY'")
    String getDiscordShutdownDelay();
}
