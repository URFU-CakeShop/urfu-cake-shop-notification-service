package ru.urfu.cake.shop.notification.service;

import java.util.Map;

/**
 * Performs placeholder substitution in template strings.
 * Placeholder syntax: {@code ${key}}.
 */
public interface TemplateProcessor {

    /**
     * Replaces all {@code ${key}} placeholders in {@code template}
     * with corresponding values from {@code variables}.
     * Unknown placeholders are left as-is and a warning is logged.
     *
     * @param template  template string, may be null (returns empty string)
     * @param variables substitution map, may be null (returns template unchanged)
     * @return processed string
     */
    String process(String template, Map<String, Object> variables);
}