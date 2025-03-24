package com.streamTracker.database.user;

import com.streamTracker.database.DatabaseReader;
import com.streamTracker.database.model.DatabaseUserModel;
import lombok.NonNull;
import org.apache.ibatis.session.SqlSessionFactory;
import org.jetbrains.annotations.Nullable;

/**
 * Class for accessing, creating and modifying users in database.
 */
public class UserDAO extends DatabaseReader<IUserMapper> {

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
    public DatabaseUserModel findUserByName(@NonNull String name) {
        return get((mapper -> mapper.findUserByName(name)));
    }

    /**
     * Finds user by Twitch ID.
     * 
     * @param twitchId Twitch ID of the user.
     * @return The user if exists.
     */
    @Nullable
    public DatabaseUserModel findUserByTwitchId(long twitchId) {
        return get((mapper -> mapper.findUserByTwitchId(twitchId)));
    }

    /**
     * Creates new user.
     * 
     * @param user User data.
     */
    public void insertUser(@NonNull DatabaseUserModel user) {
        insert(mapper -> mapper.insertUser(user));
    }

    /**
     * Finds user by its ID.
     * 
     * @param id Database ID of the user.
     * @return The user if exists.
     */
    @Nullable
    public DatabaseUserModel findUserById(int id) {
        return get(mapper -> mapper.findUserById(id));
    }
}
