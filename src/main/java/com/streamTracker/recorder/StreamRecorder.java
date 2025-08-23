package com.streamTracker.recorder;

import com.streamTracker.ApplicationProperties;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

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

        String fileName = this.fileController.vodFilePath(streamName);
        String logName = this.fileController.logFilePath(streamName);

        try {
            BufferedReader i = new BufferedReader(
                    new InputStreamReader(
                            Runtime.getRuntime()
                                    .exec(new String[]{cmdLine[0], cmdLine[1], "streamlink https://www.twitch.tv/" + streamName + " " + this.properties.getVodResolution() + " --stream-url"})
                                    .getInputStream()
                    )
            );

            i.lines().forEach(e -> {
                log.debug("Stream link to recording: {}", e);
                try {
                    Runtime.getRuntime().exec(new String[]{cmdLine[0], cmdLine[1], "ffmpeg -i \"" + e + "\" -preset veryfast " + fileName + " 2> " + logName}).getInputStream();
                    log.debug("Recording...");
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
        } catch (Exception e) {
            log.error("Couldn't initialize recording.", e);
        }
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
