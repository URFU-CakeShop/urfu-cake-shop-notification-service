package ru.urfu.cake.shop.notification.service;

import jakarta.mail.MessagingException;
import ru.urfu.cake.shop.notification.dto.Request.EmailRequest;

/**
 * Contract for sending email notifications.
 * Implementations are responsible only for transport — content resolution
 * is delegated to {@link EmailContentResolver}.
 */
public interface EmailService {

    /**
     * Sends an HTML email based on the given request.
     * Content is resolved via template if {@code request.type} is provided;
     * otherwise {@code request.subject} and {@code request.htmlContent} are used directly.
     *
     * @param request notification data
     * @throws MessagingException       if a JavaMail transport error occurs
     * @throws IllegalArgumentException if required fields are missing
     */
    void sendHtmlEmail(EmailRequest request) throws MessagingException;
}