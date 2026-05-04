package ru.urfu.cake.shop.notification.service;

import ru.urfu.cake.shop.notification.dto.Request.EmailTemplateRequest;
import ru.urfu.cake.shop.notification.dto.Response.EmailTemplateResponse;
import ru.urfu.cake.shop.notification.entity.EmailTemplate;

import java.util.List;
import java.util.Optional;

/**
 * Contract for managing email templates.
 */
public interface TemplateService {

    List<EmailTemplateResponse> getAll();

    EmailTemplateResponse save(EmailTemplateRequest request);

    void deleteByKey(String key);

    Optional<EmailTemplate> findByKey(String key);

    EmailTemplate getByKeyOrThrow(String key);
}