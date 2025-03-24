package com.streamTracker.database.user;

import com.streamTracker.database.model.DatabaseUserModel;
import lombok.NonNull;
import org.apache.ibatis.annotations.Arg;
import org.apache.ibatis.annotations.ConstructorArgs;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

public interface IUserMapper {

    @Select("SELECT * FROM stream_tracker.users WHERE name=#{name}")
    @Results(id = "user")
    @ConstructorArgs({
            @Arg(column = "id", javaType = Integer.class),
            @Arg(column = "discord_id", javaType = Long.class),
            @Arg(column = "name", javaType = String.class),
            @Arg(column = "twitch_id", javaType = Long.class)
    })
    DatabaseUserModel findUserByName(@NonNull String name);
 
    @Select("SELECT * FROM stream_tracker.users WHERE twitch_id=#{twitchId}")
    @ResultMap("user")
    DatabaseUserModel findUserByTwitchId(long twitchId);

    @Select("SELECT * FROM stream_tracker.users WHERE id=#{id}")
    @ResultMap("user")
    DatabaseUserModel findUserById(int id);

    @Insert("INSERT INTO stream_tracker.users (discord_id, name, twitch_id) VALUES (#{discordId}, #{name}, #{twitchId})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void insertUser(@NonNull DatabaseUserModel model);
}
