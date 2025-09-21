package com.streamTracker.recorder;

import com.streamTracker.ApplicationProperties;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Recorder for twitch streams.
 */
@Slf4j @RequiredArgsConstructor
public class StreamRecorder {

    /**
     * Controller that provides operations on files and directories for recording.
     */
    @NonNull
    private final FileController fileController;

    /**
     * CMD prefix depending on operating system.
     */
    @NonNull
    private final String[] cmdLine = System.getProperty("os.name").toLowerCase().contains("windows") ? new String[]{"cmd.exe", "/c"} : new String[]{"/bin/sh", "-c"};

    /**
     * Properties of the application.
     */
    @NonNull
    private final ApplicationProperties properties;

    /**
     * List of the stream currently being recorded.
     */
    @NonNull
    private final Map<String, LocalDateTime> recordingStreams = new ConcurrentHashMap<>();

    /**
     * Starts recording of a twitch stream.
     *
     * @param streamName Name of twitch stream to record.
     */
    public void record(@NonNull String streamName) {
        if (this.fileController.getVodDirectory().mkdirs()) {
            log.debug("Directory initialized.");
        } else {
            makeSpace();
        }
        log.debug("Recording initialization. {}", streamName);

        if (recordingStreams.get(streamName) == null) {
            recordingStreams.put(streamName, LocalDateTime.now());
            new Thread(() -> executeCommands(streamName)).start();
        } else {
            log.trace("Stream already being recorded. {}", streamName);
        }
    }

    /**
     * Calls commands to start stream recording.
     *
     * @param streamName Name of twitch stream to record.
     */
    private void executeCommands(@NonNull String streamName) {
        try {
            String link = new BufferedReader(
                    new InputStreamReader(
                            Runtime.getRuntime()
                                    .exec(new String[]{cmdLine[0], cmdLine[1], "streamlink https://www.twitch.tv/" + streamName + " " + this.properties.getVodResolution() + " --stream-url"})
                                    .getInputStream()))
                    .lines()
                    .findFirst()
                    .orElse(null);

            if (link == null || link.startsWith("error")) {
                if (this.recordingStreams.get(streamName).plus(this.properties.getStreamRecordingRetry()).isBefore(LocalDateTime.now())) {
                    this.recordingStreams.remove(streamName);
                    log.info("Couldn't get link for '{}' in time. Error: {}", streamName, link);
                } else {
                    Thread.sleep(5000);
                    executeCommands(streamName);
                }
            } else {
                log.debug("Stream link to recording: {}", link);
                String fileName = this.fileController.vodFilePath(streamName);
                String logName = this.fileController.logFilePath(streamName);
                Runtime.getRuntime().exec(new String[]{cmdLine[0], cmdLine[1], "ffmpeg -i \"" + link + "\" -preset veryfast " + fileName + " 2> " + logName}).getInputStream();
                log.debug("Recording...");
            }
        } catch (Exception e) {
            log.error("Couldn't initialize recording.", e);
        }
    }

    /**
     * Marks stream as offline so the recording might start again.
     *
     * @param streamName Name of twitch stream to record.
     */
    public void streamOffline(@NonNull String streamName) {
        log.trace("Removing stream from recording list. {}", streamName);
        this.recordingStreams.remove(streamName);
    }

    /**
     * Finds if there is enough space on disk based on {@link ApplicationProperties#getSpaceThreshold()}.
     * If not, removes oldest recordings until enough available space.
     */
    private void makeSpace() {
        long usableSpace = this.fileController.getVodDirectory().getUsableSpace() / (1024 * 1024 * 1024);
        log.debug("Usable space is {} GB", usableSpace);
        try {
            while (usableSpace < this.properties.getSpaceThreshold()) {
                File file = this.fileController.getOldestFile();
                if (file == null) {
                    log.warn("No files to delete while usable space low.");
                    break;
                }
                log.warn("Insufficient space. File {} was deleted: {}", file.getName(), file.delete());
                usableSpace = this.fileController.getVodDirectory().getUsableSpace() / (1024 * 1024 * 1024);
            }
        } catch (Exception e) {
            log.error("Space check or file deletion failed.", e);
        }
    }
}
