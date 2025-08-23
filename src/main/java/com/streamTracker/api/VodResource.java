package com.streamTracker.api;

import com.streamTracker.recorder.FileController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.List;

@Tag(name = "VodResource", description = "Endpoints for working with recorded streams.")
@RestController
@RequestMapping("/vods")
@RequiredArgsConstructor
public class VodResource {

    /**
     * Controller that provides operations on files and directories for recording.
     */
    @NonNull
    private final FileController fileController;

    @GetMapping("/download")
    @Operation(summary = "Download recorded stream.", description = "Download latest recorded stream or a specific stream if you provide valid name.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Requested video file", content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE)),
            @ApiResponse(responseCode = "404", description = "Requested file or no file found.")
    })
    public ResponseEntity<Resource> getVod(@RequestParam(name = "vodName") @Parameter(description = "Name of the vod to be returned.") @Nullable String vodName) {
        File file;
        if (vodName != null) {
            file = this.fileController.getAllVodFiles().stream()
                    .filter(f -> f.getName().equals(vodName))
                    .findAny()
                    .orElse(null);
        } else {
            file = this.fileController.getLatestVod();
        }

        if (file == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getName() + "\"")
                .body(new FileSystemResource(file));
    }

    @GetMapping
    @Operation(summary = "Get all recorded streams.", description = "Returns names of all available recorded streams.")
    @ApiResponse(responseCode = "200", description = "List of all available recordings. May be omitted if you want to get last recording.")
    public ResponseEntity<List<String>> getAvailableVodNames() {
        return ResponseEntity.ok(this.fileController.getAllVodFiles().stream()
                .map(File::getName)
                .toList());
    }
}
