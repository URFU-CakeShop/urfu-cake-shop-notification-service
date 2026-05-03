package ru.urfu.cake.shop.notification.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.urfu.cake.shop.notification.dto.EmailRequest;
import ru.urfu.cake.shop.notification.service.EmailService;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
@Tag(name = "Notification sender", description = "Отправляет сообщение")
public class SenderController {

    private final EmailService emailService;

    @PostMapping
    @Operation(summary = "Отправить email напрямую (тест)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Письмо успешно отправлено"),
            @ApiResponse(responseCode = "500", description = "Ошибка SMTP при отправке")
    })
    public ResponseEntity<String> emailRequest(@RequestBody EmailRequest request) {
        try {
            emailService.sendHtmlEmail(request);
            return ResponseEntity.ok("Сообщение успешно отправлено");
        } catch (MessagingException e) {
            return ResponseEntity.status(500).body("Ошибка при отправке: " + e.getMessage());
        }
    }
}
