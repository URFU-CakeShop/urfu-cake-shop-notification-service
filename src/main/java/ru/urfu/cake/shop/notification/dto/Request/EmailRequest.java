package ru.urfu.cake.shop.notification.dto.Request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for email notification data.
 * Used both in Kafka Streams pipeline and REST endpoint.
 *
 * <p>Sending mode — either:
 * <ul>
 *   <li><b>Direct</b>: {@code subject} + {@code htmlContent} are set directly</li>
 *   <li><b>Template</b>: {@code type} is set, content is resolved via {@link ru.urfu.cake.shop.notification.service.TemplateService}</li>
 * </ul>
 */
@Data
@Schema(description = "Запрос на отправку уведомления. Поддерживает работу через шаблоны из БД или прямую передачу HTML.")
public class EmailRequest {

    @Schema(description = "ID пользователя в системе (пока что не где не используется, сойдет все)", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private UUID userId;

    @Schema(description = "Ключ шаблона из базы данных (например, ORDER_CONFIRMED). Если заполнен, поля subject и htmlContent игнорируются.",
            example = "ORDER_CONFIRMED")
    private String type;

    @Schema(description = "Технический статус уведомления (пока что не где не используется, сойдет все)", example = "PENDING")
    private String status;

    @Schema(description = "Данные для подстановки в шаблон. Ключи должны соответствовать переменным {{key}} в теле шаблона.",
            example = "{\"orderId\": \"12345\", \"name\": \"Alrons\", \"totalAmount\": \"1500\"}")
    private Map<String, Object> payload;

    @NotBlank(message = "Recipient email must not be blank")
    @Email(message = "Recipient email must be a valid email address")
    @Schema(description = "Email получателя", example = "customer@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String to;

    @Schema(description = "Тема письма. Используется только если не задан 'type' (Direct Mode).",
            example = "Ваш заказ готов!")
    private String subject;

    @Schema(description = "Полный HTML код письма. Используется только если не задан 'type' (Direct Mode).",
            example = "<html><body><h1>Привет!</h1></body></html>")
    private String htmlContent;
}