package com.streamTracker.database.twitch;

import com.streamTracker.database.model.DatabaseStreamModel;
import com.streamTracker.database.model.DatabaseUserModel;
import com.streamTracker.database.model.UserRegistrationModel;
import lombok.NonNull;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.math.BigInteger;
import java.util.List;

interface ITwitchBotMapper {

    @Select("SELECT EXISTS(SELECT 1 FROM stream_tracker.discord_users WHERE discord_id=#{discordId})")
    boolean userExist(long discordId);

    @Select("SELECT * FROM stream_tracker.discord_users")
    @Results( {
        @Result(property = "discordId", column = "discord_id", id = true),
        @Result(property = "name", column = "name")
    })
    List<DatabaseUserModel> getUsers();

    @Select("SELECT * FROM stream_tracker.twitch_streams WHERE name=#{streamName}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "name", column = "name")
    })
    List<DatabaseStreamModel> getStream(@NonNull String streamName);

    @Select("SELECT * FROM stream_tracker.twitch_streams")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "name", column = "name")
    })
    List<DatabaseStreamModel> getStreams();

    @Select("SELECT discord_id, name, stream_prediction_enabled, recorder_enabled FROM stream_tracker.discord_users " +
        "JOIN stream_tracker.twitch_streams_discord_users_rel USING(discord_id) " +
        "WHERE twitch_id = #{id}")
    @Results({
        @Result(property = "discordId", column = "discord_id", id = true),
        @Result(property = "name", column = "name"),
        @Result(property = "enableStreamPredict", column = "stream_prediction_enabled"),
        @Result(property = "recordStream", column = "recorder_enabled")
    })
    List<DatabaseUserModel> getChannelUsers(@NonNull Integer id);

    @Insert("INSERT INTO stream_tracker.discord_users (discord_id, name) VALUES (#{discordId}, #{userName})")
    void insertUser(@NonNull UserRegistrationModel model);

    @Insert("INSERT INTO stream_tracker.twitch_streams (name) VALUES (#{streamName})")
    @Options(useGeneratedKeys = true, keyProperty = "streamId", keyColumn = "id")
    void insertStream(@NonNull UserRegistrationModel model);

    @Insert("INSERT INTO stream_tracker.twitch_streams_discord_users_rel (twitch_id, discord_id, recorder_enabled, stream_prediction_enabled) VALUES (#{streamId}, #{discordId}, #{recordStream}, #{streamPrediction})")
    void insertStreamUserRel(@NonNull UserRegistrationModel model);

    @Select("SELECT EXISTS(SELECT 1 FROM stream_tracker.twitch_streams_discord_users_rel WHERE twitch_id=#{streamId} AND discord_id=#{discordId})")
    boolean relExist(@NonNull @Param("streamId") Integer streamId, @Param("discordId") long discordId);
}
