package com.zorpengur.notification;

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
    private static final File DIR = new File(Main.HOME, "VODs");
    /**
     * Date format for naming files.
     */
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd-HHmmss");
    /**
     * CMD prefix depending on operating system.
     */
    private static final String CMD_LINE = System.getProperty("os.name").toLowerCase().contains("windows") ? "cmd.exe /c" : "/bin/sh -c";
    /**
     * Pattern to get date format from file names.
     */
    private static final Pattern PATTERN = Pattern.compile("-\\d+-\\d+\\.");
    /**
     * Minimum of available space on disk before recording. (in GB)
     */
    private static final int SPACE_THRESHOLD = 10;

    static {
        if (DIR.mkdirs()) {
            log.debug("Directory initialize.");
        }
    }

    /**
     * Starts recording of a twitch stream.
     *
     * @param streamName Name of twitch stream to record.
     */
    public static void record(String streamName) {
        log.debug("Recording initialization. {}", streamName);
        makeSpace();

        String fileName = DIR.getAbsolutePath() + "/VOD_" + streamName + "-" + DATE_FORMAT.format(new Date()) + ".mkv";
        String logName = DIR.getAbsolutePath() + "/LOG_" + streamName + "-" + DATE_FORMAT.format(new Date()) + ".txt";

        try {
            BufferedReader i = new BufferedReader(
                new InputStreamReader(
                    Runtime.getRuntime().exec(CMD_LINE + " streamlink https://www.twitch.tv/" + streamName + " best --stream-url --twitch-access-token-param KEY=5ouehqg1k6s9ohuchq40plojc2bqac")
                        .getInputStream()
                )
            );

            i.lines().forEach(e -> {
                log.debug("Stream link to recording: {}", e);
                try {
                    Runtime.getRuntime().exec(CMD_LINE + " ffmpeg -i \"" + e + "\" -preset veryfast " + fileName + " 2> " + logName).getInputStream();
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
     * Finds if there is enough space on disk based on {@link #SPACE_THRESHOLD}.
     * If not, removes oldest recordings until enough available space.
     */
    private static void makeSpace() {
        long usableSpace = DIR.getUsableSpace() / (1024 * 1024 * 1024);
        log.debug("Usable space is {} GB", usableSpace);
        try {
            while (usableSpace < SPACE_THRESHOLD) {
                File[] i = DIR.listFiles();
                Arrays.stream(i)
                    .min(Comparator.comparing(e -> {
                        Matcher matcher1 = PATTERN.matcher(e.getName());
                        matcher1.find();
                        return matcher1.group().replaceAll("[^0-9]", "");
                    }))
                    .map(e -> {
                        log.warn("Insufficient space. File {} was deleted.", e.getName());
                        return e.delete();
                    });
                usableSpace = DIR.getUsableSpace() / (1024 * 1024 * 1024);
            }
        } catch (Exception e) {
            log.error("Space check or file deletion failed.", e);
        }
    }
}
