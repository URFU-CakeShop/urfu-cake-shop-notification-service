package ru.urfu.cake.shop.notification.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.urfu.cake.shop.notification.dto.Request.EmailRequest;
import ru.urfu.cake.shop.notification.service.EmailService;


@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Email sending")
public class SenderController {

    private final EmailService emailService;

    @PostMapping
    @Operation(summary = "Send an email directly (for testing)")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Email sent successfully",
                    content = @Content(schema = @Schema(implementation = ru.urfu.cake.shop.notification.dto.Response.ApiResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failed / Bad request",
                    content = @Content(schema = @Schema(implementation = ru.urfu.cake.shop.notification.dto.Response.ApiResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "SMTP transport error / Internal server error",
                    content = @Content(schema = @Schema(implementation = ru.urfu.cake.shop.notification.dto.Response.ApiResponse.class))
            )
    })
    public ResponseEntity<ru.urfu.cake.shop.notification.dto.Response.ApiResponse<EmailRequest>> sendEmail(
            @Valid @RequestBody EmailRequest request) throws MessagingException {
        emailService.sendHtmlEmail(request);
        return ResponseEntity.ok(new ru.urfu.cake.shop.notification.dto.Response.ApiResponse<>(true, request, "Email sent successfully"));
    }


}
