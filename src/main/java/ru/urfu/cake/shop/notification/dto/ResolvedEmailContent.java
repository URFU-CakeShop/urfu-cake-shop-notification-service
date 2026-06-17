package ru.urfu.cake.shop.notification.dto;

import lombok.Value;

/**
 * Immutable result of email content resolution.
 */
@Value
public class ResolvedEmailContent {
    String subject;
    String htmlBody;
}
