package com.streamTracker.recorder;

import com.streamTracker.ApplicationProperties;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Recorder for chat messages.
 */
@Slf4j
@AllArgsConstructor
public class ChatRecorder {

    /**
     * Currently active recorders.
     */
    @NonNull
    private final Map<String, Recorder> recorders = new HashMap<>();

    /**
     * Controller that provides operations on files and directories for recording.
     */
    @NonNull
    private final FileController fileController;

    /**
     * Properties of the application.
     */
    @NonNull
    private final ApplicationProperties properties;

    /**
     * Registers streams chat to be recorded.
     *
     * @param streamName Name of the stream.
     */
    public synchronized void recordChat(@NonNull String streamName) {
        this.recorders.putIfAbsent(streamName, new Recorder(this.fileController.chatFilePath(streamName)));
    }

    /**
     * Finishes recording of the streams chat.
     *
     * @param streamName Name of the stream.
     */
    public void finishChatRecording(@NonNull String streamName) {
        Recorder recorder = this.recorders.remove(streamName);
        if (recorder != null) {
            recorder.save();
        }
    }

    /**
     * Registers message with corresponding recorder.
     *
     * @param channel Name of the channel.
     * @param time Time when the message was sent.
     * @param user Name of the user that sent the message.
     * @param message Content of the message.
     */
    public void message(@NonNull String channel, @NonNull Instant time, @NonNull String user, @NonNull String message) {
        Recorder recorder = this.recorders.get(channel);
        if (recorder != null) {
            recorder.message(time, user, message);
        }
    }

    /**
     * Records and saves messages.
     */
    @RequiredArgsConstructor
    private class Recorder {

        /**
         * List of the messages to be saved.
         */
        @NonNull
        private List<Message> messages = new ArrayList<>();

        /**
         * Path to the file with these messages.
         */
        @NonNull
        private final String filePath;

        /**
         * Puts the message to queue to be saved.
         *
         * @param time Time when the message was sent.
         * @param user Name of the user that sent the message.
         * @param message Content of the message.
         */
        public void message(@NonNull Instant time, @NonNull String user, @NonNull String message) {
            this.messages.add(new Message(time, user, message));
            if (this.messages.size() == ChatRecorder.this.properties.getChatMessageQueue()) {
                new Thread(this::save).start();
            }
        }

        /**
         * Saves the messages in queue. Needs to be synchronised to prevent file conflicts.
         */
        public synchronized void save() {
            try {
                List<Message> oldMessages = this.messages;
                this.messages = new ArrayList<>();
                Files.write(Paths.get(this.filePath),
                        (oldMessages.stream()
                                .sorted(Comparator.comparing(Message::time))
                                .map(Message::toString)
                                .collect(Collectors.joining("\n")) + "\n")
                                .getBytes(),
                        StandardOpenOption.CREATE,
                        StandardOpenOption.APPEND);
            } catch (IOException e) {
                log.warn("Couldn't create chat log file.", e);
            }
        }

        /**
         * Record of the message data.
         *
         * @param time Time when the message was sent.
         * @param user Name of the user that sent the message.
         * @param message Content of the message.
         */
        private record Message(@NonNull Instant time, @NonNull String user, @NonNull String message) {
            @NonNull
            public String toString() {
                return time().atZone(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("HH:mm")) + " " + user() + ": " + message();
            }
        }
    }
}
