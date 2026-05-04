package ru.urfu.cake.shop.notification.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.urfu.cake.shop.notification.service.TemplateProcessor;
import com.samskivert.mustache.Mustache;

import java.util.Map;

@Slf4j
@Component
public class TemplateProcessorImpl implements TemplateProcessor {

    @Override
    public String process(String template, Map<String, Object> variables) {
        if (template == null) return "";
        if (variables == null || variables.isEmpty()) return template;

        // Заполняем шаблон
        String result = Mustache.compiler()
                .defaultValue("ДАННЫЕ ОТСУТСТВУЮТ") // пока что так.
                .compile(template)
                .execute(variables);

        return result;
    }
}