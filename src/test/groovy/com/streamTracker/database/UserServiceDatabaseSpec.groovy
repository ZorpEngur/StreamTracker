package com.streamTracker.database

import com.streamTracker.DatabaseSpecBase
import com.streamTracker.database.model.DatabaseUserModel
import com.streamTracker.database.user.UserDAO
import com.streamTracker.database.user.UserService
import spock.lang.Shared

class UserServiceDatabaseSpec extends DatabaseSpecBase {

    @Shared
    UserService userService

    def setupSpec() {
        this.userService = new UserService(new UserDAO(this.sessionFactory))
    }
    
    void "get user"() {
        when:
        def user1 = this.userService.getUser(4)
        def user2 = this.userService.getUser("Fourth User")
        def user3 = this.userService.getTwitchUser(44)
        
        then:
        user1 != null
        user1 == user2
        user2 == user3
    }

    void "save new user"() {
        given:
        def user = DatabaseUserModel.builder()
                .discordId(123)
                .name("new user")
                .twitchId(1L)
                .build()
        
        when:
        this.userService.saveUser(user)
        
        then:
        with(this.userService.getUser("new user")) {
            getDiscordId() == 123
            getTwitchId() == 1L
            getId() == user.getId()
        }
    }
}
