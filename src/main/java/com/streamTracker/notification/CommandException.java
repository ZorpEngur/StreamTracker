package com.streamTracker.notification;

/**
 * Exception thrown during command parsing. Indicates that some part of the command is wrong.
 */
public class CommandException extends Exception {
    public CommandException(String message) {
        super(message);
    }
}
