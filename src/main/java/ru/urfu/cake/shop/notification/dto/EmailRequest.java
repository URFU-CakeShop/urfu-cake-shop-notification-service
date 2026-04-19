package ru.urfu.cake.shop.notification.dto;

import lombok.Data;

@Data
public class EmailRequest {
    private String to;

    private String subject;

    private String htmlContent;

}
