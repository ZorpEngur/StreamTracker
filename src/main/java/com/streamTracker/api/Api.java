package com.streamTracker.api;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class Api {

    @GetMapping("/vod")
    @ApiResponse(
            responseCode = "200",
            description = "MKV video file",
            content = @Content(
                    mediaType = "application/octet-stream", // safer for Swagger
                    schema = @Schema(type = "string", format = "binary")
            )
    )
    public ResponseEntity<Resource> getInt() throws MalformedURLException {
        Path filePath = Paths.get("").resolve("VOD_pambaulettox-20250424-184248.mkv").normalize(); // "videos" is your file directory
        Resource resource = new UrlResource(filePath.toUri());
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("video/x-matroska"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"VOD_pambaulettox-20250424-184248.mkv\"")
                .body(resource);
    }
}
