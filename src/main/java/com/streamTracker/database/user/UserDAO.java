package com.streamTracker.database.user;

import com.streamTracker.database.DatabaseReader;
import com.streamTracker.database.model.UserDatabaseModel;
import lombok.NonNull;
import org.apache.ibatis.session.SqlSessionFactory;
import org.jetbrains.annotations.Nullable;

/**
 * Class for accessing, creating and modifying users in database.
 */
public class UserDAO extends DatabaseReader<UserMapper> {

    public UserDAO(@NonNull SqlSessionFactory sqlSessionFactory) {
        super(sqlSessionFactory);
    }

    /**
     * Finds user by name.
     *
     * @param name Name of the user.
     * @return The user if exists.
     */
    @Nullable
    public UserDatabaseModel findUserByName(@NonNull String name) {
        return get((mapper -> mapper.findUserByName(name)));
    }

    /**
     * Finds user by Twitch ID.
     *
     * @param twitchId Twitch ID of the user.
     * @return The user if exists.
     */
    @Nullable
    public UserDatabaseModel findUserByTwitchId(long twitchId) {
        return get((mapper -> mapper.findUserByTwitchId(twitchId)));
    }

    /**
     * Creates new user.
     *
     * @param user User data.
     */
    public void insertUser(@NonNull UserDatabaseModel user) {
        insert(mapper -> mapper.insertUser(user));
    }

    /**
     * Finds user by its ID.
     *
     * @param id Database ID of the user.
     * @return The user if exists.
     */
    @Nullable
    public UserDatabaseModel findUserById(int id) {
        return get(mapper -> mapper.findUserById(id));
    }

    /**
     * Returns whether this user conflicts with another user already saved in database.
     *
     * @param user New user.
     * @return {@code True} if user already exists.
     */
    public boolean userExists(@NonNull UserDatabaseModel user) {
        return get(mapper -> mapper.userExists(user));
    }
}
