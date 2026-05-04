package ru.urfu.cake.shop.notification.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.urfu.cake.shop.notification.dto.Request.EmailTemplateRequest;
import ru.urfu.cake.shop.notification.dto.Response.EmailTemplateResponse;
import ru.urfu.cake.shop.notification.entity.EmailTemplate;
import ru.urfu.cake.shop.notification.repository.TemplateRepository;
import ru.urfu.cake.shop.notification.service.TemplateService;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TemplateServiceImpl implements TemplateService {

    private final TemplateRepository templateRepository;

    @Override
    @Transactional
    public List<EmailTemplateResponse> getAll() {
        return templateRepository.findAll().stream()
                .map(this::buildResponse)
                .toList();
    }

    @Override
    @Transactional
    public EmailTemplateResponse save(EmailTemplateRequest request) {
        EmailTemplate template = templateRepository.findByTemplateKey(request.getTemplateKey())
                .orElseGet(EmailTemplate::new);

        template.setTemplateKey(request.getTemplateKey());
        template.setSubjectTemplate(request.getSubjectTemplate());
        template.setBodyTemplate(request.getBodyTemplate());

        EmailTemplate saved = templateRepository.save(template);
        log.info("Template '{}' saved (id={})", saved.getTemplateKey(), saved.getId());
        return buildResponse(saved);
    }

    @Override
    @Transactional
    public void deleteByKey(String key) {
        if (!templateRepository.existsByTemplateKey(key)) {
            throw new EntityNotFoundException("Template not found: " + key);
        }
        templateRepository.deleteByTemplateKey(key);
        log.info("Template '{}' deleted", key);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EmailTemplate> findByKey(String key) {
        return templateRepository.findByTemplateKey(key);
    }

    @Override
    @Transactional(readOnly = true)
    public EmailTemplate getByKeyOrThrow(String key) {
        return templateRepository.findByTemplateKey(key)
                .orElseThrow(() -> new EntityNotFoundException("Email template not found for key: " + key));
    }


    private EmailTemplateResponse buildResponse(EmailTemplate response) {
        return EmailTemplateResponse.builder().
                id(response.getId())
                .templateKey(response.getTemplateKey())
                .subjectTemplate(response.getSubjectTemplate())
                .bodyTemplate(response.getBodyTemplate())
                .createdAt(response.getCreatedAt())
                .updatedAt(response.getUpdatedAt()).build();
    }
}