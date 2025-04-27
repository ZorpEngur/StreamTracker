package com.streamTracker.database.twitch;

import com.streamTracker.database.DatabaseReader;
import com.streamTracker.database.model.StreamDatabaseModel;
import lombok.NonNull;
import org.apache.ibatis.session.SqlSessionFactory;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/**
 * Class containing database operations for twitch bots.
 */
public class TwitchBotDAO extends DatabaseReader<TwitchBotMapper> {

    public TwitchBotDAO(@NonNull SqlSessionFactory sqlSessionFactory) {
        super(sqlSessionFactory);
    }

    /**
     * Gets stream model from database.
     *
     * @param streamName Name of stream to be returned.
     * @return Stream model or null if stream doesn't exist.
     */
    @Nullable
    protected StreamDatabaseModel getStream(@NonNull String streamName) {
        return get(mapper -> mapper.getStream(streamName));
    }

    /**
     * Gets all stream models from database.
     *
     * @return List of all streams.
     */
    @NonNull
    protected List<StreamDatabaseModel> getStreams() {
        return get(TwitchBotMapper::getStreams);
    }

    /**
     * Inserts stream into database.
     *
     * @param model Stream data to insert.
     */
    protected void insertStream(@NonNull TwitchUserRelModel model) {
        insert(mapper -> mapper.insertStream(model));
    }

    /**
     * Inserts relationship of user and stream to database.
     *
     * @param model Relationship data to insert.
     */
    protected void insertRel(@NonNull TwitchUserRelModel model) {
        insert(mapper -> mapper.insertStreamUserRel(model));
    }

    /**
     * Verifies whether relationship already exists.
     *
     * @param model Relationship data to verify.
     * @return Whether relationship already exists.
     */
    protected boolean relExist(@NonNull TwitchUserRelModel model) {
        return get(mapper -> mapper.relExist(Objects.requireNonNull(model.getStreamId()), model.getUserId()));
    }

    /**
     * Retrieves all Twitch stream models from database.
     *
     * @return List of stream models.
     */
    @NonNull
    protected List<TwitchUserRelModel> getStreamModels() {
        return get(TwitchBotMapper::getStreamModels);
    }
}
