package com.streamTracker.database.twitch;

import com.streamTracker.database.DatabaseReader;
import com.streamTracker.database.model.DatabaseStreamModel;
import com.streamTracker.database.model.DatabaseUserModel;
import com.streamTracker.database.model.UserRegistrationModel;
import lombok.NonNull;
import org.apache.ibatis.session.SqlSessionFactory;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Class containing database operations for twitch bots.
 */
public class TwitchBotDAO extends DatabaseReader<ITwitchBotMapper> {

    public TwitchBotDAO(@NonNull SqlSessionFactory sqlSessionFactory) {
        super(sqlSessionFactory);
    }

    /**
     * Checks whether user is already saved in database.
     *
     * @param discordId ID of user to be verified.
     * @return Whether this ID is already saved in database.
     */
    protected boolean isUser(long discordId) {
        return get(mapper -> mapper.userExist(discordId));
    }

    /**
     * Gets stream model from database.
     *
     * @param streamName Name of stream to be returned.
     * @return Stream model or null if stream doesn't exist.
     */
    @Nullable
    protected DatabaseStreamModel getStream(@NonNull String streamName) {
        List<DatabaseStreamModel> result = get(mapper -> mapper.getStream(streamName));
        return result.isEmpty() ? null : result.get(0);
    }

    /**
     * Gets all stream models from database.
     *
     * @return List of all streams.
     */
    @NonNull
    protected List<DatabaseStreamModel> getStreams() {
        return get(ITwitchBotMapper::getStreams);
    }

    /**
     * Returns list of all users registered for this stream.
     *
     * @param id ID of the stream.
     * @return List of all user models.
     */
    @NonNull
    protected List<DatabaseUserModel> getChannelUsers(@NonNull Integer id) {
        return get(mapper -> mapper.getChannelUsers(id));
    }

    /**
     * Inserts user into database.
     *
     * @param model User data to insert.
     */
    protected void insertUser(@NonNull UserRegistrationModel model) {
        insert(mapper -> mapper.insertUser(model));
    }

    /**
     * Inserts stream into database.
     *
     * @param model Stream data to insert.
     */
    protected void insertStream(@NonNull UserRegistrationModel model) {
        insert(mapper -> mapper.insertStream(model));
    }

    /**
     * Inserts relationship of user and stream to database.
     *
     * @param model Relationship data to insert.
     */
    protected void insertRel(@NonNull UserRegistrationModel model) {
        insert(mapper -> mapper.insertStreamUserRel(model));
    }

    /**
     * Verifies whether relationship already exists.
     *
     * @param model Relationship data to verify.
     * @return Whether relationship already exists.
     */
    protected boolean relExist(@NonNull UserRegistrationModel model) {
        return get(mapper -> mapper.relExist(model.getStreamId(), model.getDiscordId()));
    }
}
