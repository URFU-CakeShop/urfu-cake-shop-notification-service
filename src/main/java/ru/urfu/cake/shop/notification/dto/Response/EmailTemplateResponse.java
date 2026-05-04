package ru.urfu.cake.shop.notification.dto.Response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.UUID;

@Value
@Builder
@Schema(description = "Данные шаблона email-уведомления")
public class EmailTemplateResponse {

    @Schema(description = "Уникальный идентификатор шаблона в БД", example = "550e8400-e29b-41d4-a716-446655440000")
    public UUID id;

    @Schema(description = "Уникальный ключ шаблона", example = "ORDER_CONFIRMED")
    public String templateKey;

    @Schema(description = "Шаблон темы письма (поддерживает переменные {{...}})",
            example = "Заказ №{{orderId}} подтверждён! 🍰")
    public String subjectTemplate;

    @Schema(description = "HTML-шаблон тела письма (поддерживает переменные {{...}})",
            example = "<!DOCTYPE html><html>...<h2>Привет, {{name}}!</h2>...</html>")
    public String bodyTemplate;

    @Schema(description = "Дата и время создания шаблона")
    public LocalDateTime createdAt;

    @Schema(description = "Дата и время последнего обновления шаблона")
    public LocalDateTime updatedAt;
}