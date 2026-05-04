package ru.urfu.cake.shop.notification.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.urfu.cake.shop.notification.dto.Request.EmailRequest;
import ru.urfu.cake.shop.notification.dto.ResolvedEmailContent;
import ru.urfu.cake.shop.notification.entity.EmailTemplate;
import ru.urfu.cake.shop.notification.service.EmailContentResolver;
import ru.urfu.cake.shop.notification.service.TemplateProcessor;
import ru.urfu.cake.shop.notification.service.TemplateService;

/**
 * Resolves email content either from template (by {@code request.type})
 * or directly from {@code request.subject} / {@code request.htmlContent}.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EmailContentResolverImpl implements EmailContentResolver {

    private final TemplateService templateService;
    private final TemplateProcessor templateProcessor;

    @Override
    public ResolvedEmailContent resolve(EmailRequest request) {
        if (isTemplateMode(request)) {
            return resolveFromTemplate(request);
        }
        return resolveDirectly(request);
    }

    private ResolvedEmailContent resolveFromTemplate(EmailRequest request) {
        log.debug("Resolving email content from template '{}'", request.getType());
        EmailTemplate template = templateService.getByKeyOrThrow(request.getType());

        String subject = templateProcessor.process(template.getSubjectTemplate(), request.getPayload());
        String body = templateProcessor.process(template.getBodyTemplate(), request.getPayload());

        return new ResolvedEmailContent(subject, body);
    }

    private ResolvedEmailContent resolveDirectly(EmailRequest request) {
        String htmlBody = request.getHtmlContent();
        if (htmlBody == null || htmlBody.isBlank()) {
            throw new IllegalArgumentException(
                    "Email body is missing: provide 'htmlContent' directly or set 'type' to use a template."
            );
        }
        String subject = request.getSubject() != null ? request.getSubject() : "(no subject)";
        return new ResolvedEmailContent(subject, htmlBody);
    }

    private boolean isTemplateMode(EmailRequest request) {
        return request.getType() != null && !request.getType().isBlank();
    }
}