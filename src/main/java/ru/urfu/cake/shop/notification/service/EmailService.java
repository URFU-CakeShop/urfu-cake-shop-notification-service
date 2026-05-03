package ru.urfu.cake.shop.notification.service;


import jakarta.mail.MessagingException;
import ru.urfu.cake.shop.notification.dto.EmailRequest;

public interface EmailService {

    /**
     * Отправляет HTML-письмо по параметрам из {@link EmailRequest}.
     *
     * @param request данные письма: получатель, тема, HTML-контент
     * @throws MessagingException если произошла ошибка на уровне JavaMail
     */
    void sendHtmlEmail(EmailRequest request) throws MessagingException;
}
