package com.streamTracker.events;

import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Allows objects to send information about an event happening.
 * This way you can avoid tight coupling of classes that don't need to be aware of each other.
 */
public abstract class EventHandler {

    /**
     * All instances of {@link EventHandler}.
     */
    @NonNull
    private static final List<EventHandler> INSTANCES = new ArrayList<>();

    public EventHandler() {
        INSTANCES.add(this);
    }

    /**
     * This class gets called if any event happens.
     *
     * @param event The event that happened. You should always check what type of evet happened by checking
     *              what instance of sent {@link Event}.
     */
    protected void onEvent(@NonNull Event event) {
    }

    /**
     * Method that distributes this event to all instances of this class to be processed.
     *
     * @param event Instance of the specific event that happened.
     */
    protected void sendEvent(@NonNull Event event) {
        INSTANCES.parallelStream().forEach(e -> {
            if (this != e) {
                e.onEvent(event);
            }
        });
    }
}
