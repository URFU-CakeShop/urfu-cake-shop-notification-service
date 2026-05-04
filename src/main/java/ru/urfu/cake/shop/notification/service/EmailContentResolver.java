package ru.urfu.cake.shop.notification.service;

import ru.urfu.cake.shop.notification.dto.Request.EmailRequest;
import ru.urfu.cake.shop.notification.dto.ResolvedEmailContent;

/**
 * Resolves the final subject and HTML body for an email.
 * Handles both direct-content and template-based modes.
 */
public interface EmailContentResolver {

    /**
     * Resolves subject and HTML body from the request.
     *
     * @param request email request
     * @return resolved content (subject + htmlBody)
     * @throws IllegalArgumentException if content cannot be resolved
     */
    ResolvedEmailContent resolve(EmailRequest request);
}