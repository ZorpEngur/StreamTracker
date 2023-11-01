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
}
