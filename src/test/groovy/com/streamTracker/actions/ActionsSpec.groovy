package com.streamTracker.actions

import com.streamTracker.ApplicationProperties
import com.streamTracker.api.SystemResourceApi
import lombok.Getter
import reactor.core.publisher.Flux
import spock.lang.Specification

import java.time.Duration
import java.time.LocalDateTime
import java.util.stream.Stream

class ActionsSpec extends Specification {

    void "Test calling action"() {
        given:
        def properties = Mock(ApplicationProperties) {
            getFallbackUrl() >> "http://localhost:8080/"
            getFallbackWaitTime() >> Duration.ofSeconds(1)
            getMessageDelay() >> Duration.ofMinutes(5)
            getActionQueSize() >> 100
        }
        def action1 = new com.streamTracker.api.model.Action().version(1).filterDuplicit(true)
                .code("ACTION_1").body("Filter duplicit on").date(LocalDateTime.of(2026, 1, 1, 12, 0))
        def action2 = new com.streamTracker.api.model.Action().version(1).filterDuplicit(false)
                .code("ACTION_2").body("Filter duplicit off").date(LocalDateTime.of(2026, 1, 1, 12, 0))
        def systemResourceApi = Mock(SystemResourceApi) {
            getActions() >> Flux.fromStream(Stream.of(action1, action2))
        }
        def implementation = new TestImplementation(properties, systemResourceApi)

        when:
        def response = implementation.run(action)

        then:
        response == result

        where:
        result | action
        false  | new Action(1, "ACTION_1", true, "Filter duplicit on", LocalDateTime.of(2026, 1, 1, 12, 0))
        true   | new Action(1, "ACTION_2", false, "Filter duplicit off", LocalDateTime.of(2026, 1, 1, 12, 0))
        true   | new Action(2, "ACTION_1", true, "Filter duplicit on", LocalDateTime.of(2026, 1, 1, 12, 0))
        true   | new Action(1, "ACTION_1", true, "Different message", LocalDateTime.of(2026, 1, 1, 12, 0))
        true   | new Action(1, "ACTION_1", true, "Filter duplicit on", LocalDateTime.of(2026, 1, 1, 13, 0))

    }

    class TestImplementation implements ActionsInterface {

        @Getter
        ActionsService actionsService

        @Getter
        ApplicationProperties properties

        TestImplementation(ApplicationProperties properties, SystemResourceApi systemResourceApi) {
            this.actionsService = new ActionsService(properties, systemResourceApi)
            this.properties = properties
        }

        boolean run(Action action1) {
            return action(action1, false)
        }
    }
}
