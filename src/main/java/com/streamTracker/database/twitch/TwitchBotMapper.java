package com.streamTracker.database.twitch;

import com.streamTracker.database.model.NotificationPlatform;
import com.streamTracker.database.model.StreamDatabaseModel;
import com.streamTracker.database.model.handlers.NotificationPlatformHandler;
import lombok.NonNull;
import org.apache.ibatis.annotations.Arg;
import org.apache.ibatis.annotations.ConstructorArgs;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface TwitchBotMapper {

    @Select("SELECT * FROM stream_tracker.twitch_streams WHERE name=#{streamName}")
    @Results(id = "stream")
    @ConstructorArgs({
            @Arg(column = "id", javaType = Integer.class),
            @Arg(column = "name", javaType = String.class)
    })
    StreamDatabaseModel getStream(@NonNull String streamName);

    @Select("SELECT * FROM stream_tracker.twitch_streams")
    @ResultMap("stream")
    List<StreamDatabaseModel> getStreams();

    @Insert("INSERT INTO stream_tracker.twitch_streams (name) VALUES (#{streamName})")
    @Options(useGeneratedKeys = true, keyProperty = "streamId", keyColumn = "id")
    void insertStream(@NonNull TwitchUserRelModel model);

    @Insert("INSERT INTO stream_tracker.twitch_streams_users_rel (twitch_id, user_id, recorder_enabled, stream_prediction_enabled, platform_id) VALUES (#{streamId}, #{userId}, #{recordStream}, #{streamPrediction}, #{notificationPlatform.id})")
    void insertStreamUserRel(@NonNull TwitchUserRelModel model);

    @Select("SELECT EXISTS(SELECT 1 FROM stream_tracker.twitch_streams_users_rel WHERE twitch_id=#{streamId} AND user_id=#{userId})")
    boolean relExist(@NonNull @Param("streamId") Integer streamId, @Param("userId") long userId);

    @Select("SELECT * FROM stream_tracker.twitch_streams_users_rel AS rel LEFT JOIN stream_tracker.twitch_streams AS stream ON stream.id = rel.twitch_id")
    @ConstructorArgs({
            @Arg(column = "twitch_id", javaType = Integer.class),
            @Arg(column = "name", javaType = String.class),
            @Arg(column = "user_id", javaType = int.class),
            @Arg(column = "recorder_enabled", javaType = boolean.class),
            @Arg(column = "stream_prediction_enabled", javaType = boolean.class),
            @Arg(column = "platform_id", javaType = NotificationPlatform.class, typeHandler = NotificationPlatformHandler.class)
    })
    List<TwitchUserRelModel> getStreamModels();
}
