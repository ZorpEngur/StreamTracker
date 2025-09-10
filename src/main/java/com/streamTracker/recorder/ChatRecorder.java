package com.streamTracker.recorder;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
public class ChatRecorder {

    @NonNull
    private final Map<String, Recorder> recorders = new HashMap<>();

    @NonNull
    private final FileController fileController;

    public synchronized void recordChat(@NonNull String streamName) {
        recorders.putIfAbsent(streamName, new Recorder(this.fileController.chatFilePath(streamName)));
    }

    public void finishChatRecording(@NonNull String streamName) {
        Recorder recorder = recorders.remove(streamName);
        if (recorder != null) {
            recorder.save();
        }
    }

    public void message(@NonNull String channel, @NonNull Instant time, @NonNull String user, @NonNull String message) {
        Recorder recorder = this.recorders.get(channel);
        if (recorder != null) {
            recorder.message(time, user, message);
        }
    }

    @RequiredArgsConstructor
    private static class Recorder {

        @NonNull
        private List<Message> messages = new ArrayList<>();

        @NonNull
        private final String fileName;

        public void message(@NonNull Instant time, @NonNull String user, @NonNull String message) {
            messages.add(new Message(time, user, message));
            if (messages.size() == 1000) {
                new Thread(this::save).start();
            }
        }

        public void save() {
            try {
                List<Message> oldMessages = this.messages;
                this.messages = new ArrayList<>();
                Files.write(Paths.get(this.fileName),
                        oldMessages.stream()
                                .map(Message::toString)
                                .collect(Collectors.joining("\n"))
                                .getBytes(),
                        StandardOpenOption.APPEND,
                        StandardOpenOption.CREATE);
            } catch (IOException e) {
                log.warn("Couldn't create chat log file.", e);
            }
        }

        private record Message(@NonNull Instant time, @NonNull String user, @NonNull String message) {
            @NonNull
            public String toString() {
                return time().atZone(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("HH:mm")) + " " + user() + ": " + message();
            }
        }
    }
}
