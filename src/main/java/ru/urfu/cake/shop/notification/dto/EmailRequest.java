package ru.urfu.cake.shop.notification.dto;

import lombok.Data;


/**
 * DTO для передачи данных email-уведомления.
 * Используется как в Kafka Streams pipeline, так и в REST-эндпоинте.
 *
 * <p>Поля не имеют валидационных аннотаций намеренно:
 * фильтрация null/blank происходит в {@code NotificationStreamProcessor}
 * до вызова сервиса.
 */
@Data
public class EmailRequest {

    /** Email-адрес получателя. */
    private String to;

    /** Тема письма. */
    private String subject;

    /** Тело письма в формате HTML. */
    private String htmlContent;

}
