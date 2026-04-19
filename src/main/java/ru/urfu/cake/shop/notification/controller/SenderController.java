package ru.urfu.cake.shop.notification.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.urfu.cake.shop.notification.dto.EmailRequest;
import ru.urfu.cake.shop.notification.service.EmailService;
import org.springframework.http.ResponseEntity;

@Controller
@RequestMapping("/notification")
@RequiredArgsConstructor
@Tag(name = "Notification sender", description = "Отправляет сообщение")
public class SenderController {

    private final EmailService emailService;

    @PostMapping
    @Operation(summary = "Отпраляет сообщение через email")
    public ResponseEntity<String> emailRequest(@RequestBody EmailRequest request) { // Исправлен синтаксис
        try {
            emailService.sendHtmlEmail(request);
            return ResponseEntity.ok("Сообщение успешно отправлено");
        } catch (MessagingException e) {
            return ResponseEntity.status(500).body("Ошибка при отправке: " + e.getMessage());
        }
    }
}
