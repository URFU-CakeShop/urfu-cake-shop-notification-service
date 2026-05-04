package ru.urfu.cake.shop.notification.dto.Request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmailTemplateRequest {

    @NotBlank(message = "templateKey must not be blank")
    private String templateKey;

    @NotBlank(message = "subjectTemplate must not be blank")
    private String subjectTemplate;

    @NotBlank(message = "bodyTemplate must not be blank")
    private String bodyTemplate;
}