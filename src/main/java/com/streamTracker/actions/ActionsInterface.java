package com.streamTracker.actions;

import com.streamTracker.ApplicationProperties;
import lombok.NonNull;

/**
 * Interface providing actions operations to have fallback services.
 */
public interface ActionsInterface {

    /**
     * Logs action and checks if the execution should continue.
     *
     * @param action Action that will be run.
     * @param skipWait If the wait before check should be skipped.
     * @return {@code True} if the action should be executed, otherwise {@code False}.
     */
    default boolean action(@NonNull Action action, boolean skipWait) {
        getActionsService().addAction(action);
        if (action.isFilterDuplicit() && getProperties().getFallbackUrl() != null) {
            if (!skipWait) {
                try {
                    Thread.sleep(getProperties().getFallbackWaitTime().toMillis());
                } catch (InterruptedException ignored) {}
            }
            return !getActionsService().checkAction(action);
        }
        return true;
    }

    /**
     * Properties of the application.
     */
    @NonNull
    ApplicationProperties getProperties();

    /**
     * Actions service.
     */
    @NonNull
    ActionsService getActionsService();
}
