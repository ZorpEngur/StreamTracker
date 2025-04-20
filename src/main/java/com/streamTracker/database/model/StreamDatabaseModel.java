package com.streamTracker.database.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Model representing stream in database.
 */
@Getter
@RequiredArgsConstructor
public class StreamDatabaseModel {

    /**
     * Database ID of the stream.
     */
    @NonNull
    private final Integer id;

    /**
     * Name of the stream.
     */
    @NonNull
    private final String name;
}
