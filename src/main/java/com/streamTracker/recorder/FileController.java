package com.streamTracker.recorder;

import com.streamTracker.ApplicationProperties;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AllArgsConstructor
public class FileController {

    /**
     * Pattern to get date format from file names.
     */
    @NonNull
    private final Pattern pattern = Pattern.compile("-\\d+-\\d+\\.");

    /**
     * Date format for naming files.
     */
    @NonNull
    private final DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");

    /**
     * Directory where recordings will be saved.
     */
    @NonNull
    @Getter
    private final File vodDirectory;

    public FileController(@NonNull ApplicationProperties properties) {
        this.vodDirectory = new File(properties.getFilePath(), "VODs");
    }

    /**
     * Creates file path of the vod.
     *
     * @param streamName Name of the stream.
     * @return Full path with name of the vod.
     */
    @NonNull
    public String vodFilePath(@NonNull String streamName) {
        return getVodDirectory().getAbsolutePath() + "/VOD_" + streamName + "-" + dateFormat.format(new Date()) + ".mkv";
    }

    /**
     * Creates file path of the log.
     *
     * @param streamName Name of the stream.
     * @return Full path with name of the log.
     */
    @NonNull
    public String logFilePath(@NonNull String streamName) {
        return getVodDirectory().getAbsolutePath() + "/LOG_" + streamName + "-" + dateFormat.format(new Date()) + ".txt";
    }

    /**
     * Creates file path of the chat log.
     *
     * @param streamName Name of the stream.
     * @return Full path with name of the chat log.
     */
    @NonNull
    public String chatFilePath(@NonNull String streamName) {
        return getVodDirectory().getAbsolutePath() + "/CHAT_" + streamName + "-" + dateFormat.format(new Date()) + ".txt";
    }

    /**
     * Finds all files in vod directory.
     */
    @NonNull
    public List<File> getAllFiles() {
        File[] files = getVodDirectory().listFiles();
        if (files != null) {
            return Arrays.asList(files);
        }
        return List.of();
    }

    /**
     * Finds all vod files.
     */
    @NonNull
    public List<File> getAllVodFiles() {
        return getAllFiles().stream()
                .filter(f -> f.getName().startsWith("VOD"))
                .toList();
    }

    /**
     * Finds oldest log or vod file.
     */
    @Nullable
    public File getOldestFile() {
        return getAllFiles().stream()
                .min(Comparator.comparing(f -> {
                    Matcher matcher1 = this.pattern.matcher(f.getName());
                    matcher1.find();
                    return matcher1.group().replaceAll("[^0-9]", "");
                })).orElse(null);
    }

    /**
     * Finds newest vod file.
     */
    @Nullable
    public File getLatestVod() {
        return getAllFiles().stream()
                .filter(f -> f.getName().startsWith("VOD"))
                .max(Comparator.comparing(f -> {
                    Matcher matcher1 = this.pattern.matcher(f.getName());
                    matcher1.find();
                    return matcher1.group().replaceAll("[^0-9]", "");
                })).orElse(null);
    }
}
