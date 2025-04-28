package com.streamTracker.events

import lombok.NonNull
import org.jetbrains.annotations.NotNull
import spock.lang.Specification
import spock.lang.Timeout

class EventHandlerSpec extends Specification {

    void "Subscriber gets called"() {
        given:
        def s1 = new Subscriber()
        def s2 = new Subscriber()

        when:
        s1.sendEvent(new NewNotificationEvent())
        Thread.sleep(1000)

        then:
        s2.executed
        !s1.executed
    }

    @Timeout(10)
    void "Implementations of EventHandler should not block each other"() {
        given:
        def sd1 = new SubscriberDelay()
        def sd2 = new SubscriberDelay()
        def s3 = new Subscriber()

        when:
        s3.sendEvent(new NewNotificationEvent())
        Thread.sleep(1000)

        then:
        !sd1.executed
        !sd2.executed
        !s3.executed
    }

    private class Subscriber extends EventHandler {
        public boolean executed = false
        @Override
        protected void onEvent(@NotNull @NonNull Event event) {
            this.executed = true
        }
    }
    private class SubscriberDelay extends EventHandler {
        public boolean executed = false
        @Override
        protected void onEvent(@NotNull @NonNull Event event) {
            Thread.sleep(30000)
            this.executed = true
        }
    }
}
