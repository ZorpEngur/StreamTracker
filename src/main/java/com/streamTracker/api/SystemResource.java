package com.streamTracker.api;

import com.streamTracker.actions.Action;
import com.streamTracker.actions.ActionsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "SystemResource", description = "Endpoints for getting system information.")
@RestController
@RequestMapping("/system")
@RequiredArgsConstructor
public class SystemResource {

    /**
     * Acton service.
     */
    private final ActionsService actionsService;

    @GetMapping("/actions")
    @Operation(summary = "Get list of all recent actions.", description = "Returns list off all recent actions ran by the server.")
    @ApiResponse(responseCode = "200", description = "List of all recent actions.")
    public ResponseEntity<List<Action>> getActions() {
        return ResponseEntity.ok(this.actionsService.getActions());
    }
}
