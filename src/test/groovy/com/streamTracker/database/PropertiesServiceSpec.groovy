package com.streamTracker.database

import com.streamTracker.SpecBase
import com.streamTracker.database.properties.PropertiesService

import java.time.Duration
import java.time.temporal.ChronoUnit

class PropertiesServiceSpec extends SpecBase {

    void "Should get discord token"() {
        expect:
        PropertiesService.getInstance().getDiscordToken() == "316677868552437022"
    }

    void "Should get file path"() {
        expect:
        PropertiesService.getInstance().getFilePath() == "~/bot/"
    }

    void "Should get managing channel"() {
        expect:
        PropertiesService.getInstance().getManageChannel() == "Test name"
    }

    void "Should get twitch name"() {
        expect:
        PropertiesService.getInstance().getTwitchName() == "Test Name"
    }

    void "Should get twitch token"() {
        expect:
        PropertiesService.getInstance().getTwitchToken() == "cxa8lcma6wi5r953qphqhmb4j9lzka"
    }

    void "Should get vod resolution"() {
        expect:
        PropertiesService.getInstance().getVodResolution() == "720p,480p,best"
    }

    void "Should get message delay"() {
        expect:
        PropertiesService.getInstance().getMessageDelay() == 1
    }

    void "Should get space threshold"() {
        expect:
        PropertiesService.getInstance().getSpaceThreshold() == 10
    }

    void "Should get Discord shutdown delay"() {
        expect:
        PropertiesService.getInstance().getDiscordShutdownDelay() == Duration.of(10, ChronoUnit.MINUTES)
    }
}
