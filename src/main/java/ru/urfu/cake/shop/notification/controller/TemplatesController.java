package ru.urfu.cake.shop.notification.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.urfu.cake.shop.notification.dto.Request.EmailTemplateRequest;
import ru.urfu.cake.shop.notification.dto.Response.EmailTemplateResponse;
import ru.urfu.cake.shop.notification.service.TemplateService;

import java.util.List;

@RestController
@RequestMapping("/notification/templates")
@RequiredArgsConstructor
@Tag(name = "Notifications templates", description = "Template management")
public class TemplatesController {

    private final TemplateService templateService;

    @GetMapping
    @Operation(summary = "Displays all existing templates")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Templates fetched successfully",
                    content = @Content(schema = @Schema(implementation = ru.urfu.cake.shop.notification.dto.Response.ApiResponse.class))
            )
    })
    public ResponseEntity<ru.urfu.cake.shop.notification.dto.Response.ApiResponse<List<EmailTemplateResponse>>> getAllTemplates() {
        return ResponseEntity.ok(new ru.urfu.cake.shop.notification.dto.Response.ApiResponse<>(true, templateService.getAll(), "Templates fetched successfully"));
    }

    @PostMapping
    @Operation(summary = "Create or update an email template")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Template saved successfully",
                    content = @Content(schema = @Schema(implementation = ru.urfu.cake.shop.notification.dto.Response.ApiResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid template data",
                    content = @Content(schema = @Schema(implementation = ru.urfu.cake.shop.notification.dto.Response.ApiResponse.class))
            )
    })
    public ResponseEntity<ru.urfu.cake.shop.notification.dto.Response.ApiResponse<EmailTemplateResponse>> saveTemplate(
            @Valid @RequestBody EmailTemplateRequest request) {
        return ResponseEntity.ok(new ru.urfu.cake.shop.notification.dto.Response.ApiResponse<>(true, templateService.save(request), "Template saved successfully"));
    }

    @DeleteMapping("/{key}")
    @Operation(summary = "Delete an email template by key")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Template deleted successfully (No Content)"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Template not found",
                    content = @Content(schema = @Schema(implementation = ru.urfu.cake.shop.notification.dto.Response.ApiResponse.class))
            )
    })
    public ResponseEntity<Void> deleteTemplate(@PathVariable String key) {
        templateService.deleteByKey(key);
        return ResponseEntity.noContent().build();
    }
}