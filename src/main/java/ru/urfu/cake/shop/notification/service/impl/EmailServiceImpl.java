package ru.urfu.cake.shop.notification.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import ru.urfu.cake.shop.notification.dto.Request.EmailRequest;
import ru.urfu.cake.shop.notification.dto.ResolvedEmailContent;
import ru.urfu.cake.shop.notification.service.EmailContentResolver;
import ru.urfu.cake.shop.notification.service.EmailService;

/**
 * Sends HTML emails via JavaMailSender.
 * Content resolution is fully delegated to {@link EmailContentResolver}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final EmailContentResolver contentResolver;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendHtmlEmail(EmailRequest request) throws MessagingException {
        log.debug("Preparing MimeMessage for recipient={}", request.getTo());

        ResolvedEmailContent content = contentResolver.resolve(request);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(fromEmail);
        helper.setTo(request.getTo());
        helper.setSubject(content.getSubject());
        helper.setText(content.getHtmlBody(), true);

        mailSender.send(message);
        log.info("Email sent successfully to={}", request.getTo());
    }
}