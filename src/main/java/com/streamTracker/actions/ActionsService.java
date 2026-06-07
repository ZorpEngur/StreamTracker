package com.streamTracker.actions;

import com.streamTracker.ApplicationProperties;
import com.streamTracker.api.SystemResourceApi;
import lombok.NonNull;
import org.apache.commons.collections4.queue.CircularFifoQueue;

import java.util.List;
import java.util.Objects;

/**
 * Service that handles actions of the application.
 */
public class ActionsService {

    /**
     * Que of the actions performed.
     */
    @NonNull
    private final CircularFifoQueue<Action> actions;

    /**
     * Resource for calling actions from main server.
     */
    @NonNull
    private final SystemResourceApi systemResourceApi;

    /**
     * Properties of the application.
     */
    @NonNull
    private final ApplicationProperties properties;

    public ActionsService(@NonNull ApplicationProperties properties, @NonNull SystemResourceApi systemResourceApi) {
        this.properties = properties;
        this.systemResourceApi = systemResourceApi;
        this.actions = new CircularFifoQueue<>(properties.getActionQueSize());
    }

    /**
     * Adds new action to the que.
     *
     * @param action The action.
     */
    public synchronized void addAction(@NonNull Action action) {
        this.actions.add(action);
    }

    /**
     * Gets list of all actions performed by the server.
     *
     * @return List of the actions.
     */
    @NonNull
    public List<Action> getActions() {
        return this.actions.stream().toList();
    }

    /**
     * Checks if the action was performed by the main server.
     *
     * @param action The action to check.
     * @return {@code True} if the action was performed by the main server.
     */
    public boolean checkAction(@NonNull Action action) {
        Long count = this.systemResourceApi.getActions()
                .filter(a -> Objects.equals(a.getVersion(), action.getVersion()) &&
                        Objects.equals(a.getCode(), action.getCode()) &&
                        Objects.equals(a.getBody(), action.getBody()) &&
                        a.getDate().plus(this.properties.getMessageDelay()).isAfter(action.getDate())
                ).count().block();
        return count != null && count > 0;
    }
}
