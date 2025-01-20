package com.streamTracker.notification;

import com.streamTracker.ApplicationProperties;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Recorder for twitch streams.
 */
@Slf4j
public class StreamRecorder {

    /**
     * Directory where recordings will be saved.
     */
    @NonNull
    private final File dir;

    /**
     * Date format for naming files.
     */
    @NonNull
    private final DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");

    /**
     * CMD prefix depending on operating system.
     */
    @NonNull
    private final String[] cmdLine = System.getProperty("os.name").toLowerCase().contains("windows") ? new String[]{"cmd.exe", "/c"} : new String[]{"/bin/sh", "-c"};

    /**
     * Pattern to get date format from file names.
     */
    @NonNull
    private final Pattern pattern = Pattern.compile("-\\d+-\\d+\\.");

    /**
     * Properties of the application.
     */
    @NonNull
    private final ApplicationProperties properties;

    public StreamRecorder(@NonNull ApplicationProperties properties) {
        this.properties = properties;
        this.dir = new File(properties.getFilePath(), "VODs");
        if (dir.mkdirs()) {
            log.debug("Directory initialize.");
        }
    }

    /**
     * Starts recording of a twitch stream.
     *
     * @param streamName Name of twitch stream to record.
     */
    public void record(@NonNull String streamName) {
        log.debug("Recording initialization. {}", streamName);
        makeSpace();

        String fileName = dir.getAbsolutePath() + "/VOD_" + streamName + "-" + dateFormat.format(new Date()) + ".mkv";
        String logName = dir.getAbsolutePath() + "/LOG_" + streamName + "-" + dateFormat.format(new Date()) + ".txt";

        try {
            BufferedReader i = new BufferedReader(
                    new InputStreamReader(
                            Runtime.getRuntime().exec(new String[]{cmdLine[0], cmdLine[1], "streamlink https://www.twitch.tv/" + streamName + " " + this.properties.getVodResolution() + " --stream-url"})
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
        long usableSpace = dir.getUsableSpace() / (1024 * 1024 * 1024);
        log.debug("Usable space is {} GB", usableSpace);
        try {
            while (usableSpace < properties.getSpaceThreshold()) {
                File[] i = dir.listFiles();
                Arrays.stream(i)
                        .min(Comparator.comparing(e -> {
                            Matcher matcher1 = pattern.matcher(e.getName());
                            matcher1.find();
                            return matcher1.group().replaceAll("[^0-9]", "");
                        }))
                        .map(e -> {
                            log.warn("Insufficient space. File {} was deleted.", e.getName());
                            return e.delete();
                        });
                usableSpace = dir.getUsableSpace() / (1024 * 1024 * 1024);
            }
        } catch (Exception e) {
            log.error("Space check or file deletion failed.", e);
        }
    }
}
