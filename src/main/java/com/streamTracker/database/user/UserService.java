package com.streamTracker.database.user;

import com.streamTracker.database.model.UserDatabaseModel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

/**
 * Database service for users.
 */
@RequiredArgsConstructor
public class UserService {

    /**
     * Database access for the user operations.
     */
    @NonNull
    private UserDAO userDAO;

    /**
     * Saves new user to the database.
     *
     * @param user The new user.
     */
    public void saveUser(@NonNull UserDatabaseModel user) {
        this.userDAO.insertUser(user);
    }

    /**
     * Finds user by Twitch ID.
     *
     * @param twitchId Twitch ID of the user.
     * @return The user if exists.
     */
    @Nullable
    public UserDatabaseModel getTwitchUser(long twitchId) {
        return this.userDAO.findUserByTwitchId(twitchId);
    }

    /**
     * Finds user by name.
     *
     * @param name Name of the user.
     * @return The user if exists.
     */
    @Nullable
    public UserDatabaseModel getUser(@NonNull String name) {
        return this.userDAO.findUserByName(name);
    }

    /**
     * Finds user by its ID.
     *
     * @param id Database ID of the user.
     * @return The user if exists.
     */
    @Nullable
    public UserDatabaseModel getUser(int id) {
        return this.userDAO.findUserById(id);
    }

    /**
     * Returns whether this user conflicts with another user already saved in database.
     *
     * @param user New user.
     * @return {@code True} if user already exists.
     */
    public boolean userExists(@NonNull UserDatabaseModel user) {
        return this.userDAO.userExists(user);
    }
}
