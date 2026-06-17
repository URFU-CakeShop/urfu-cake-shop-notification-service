package ru.urfu.cake.shop.notification.handler;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.urfu.cake.core.handler.MessageHandler;
import ru.urfu.cake.shop.notification.dto.Request.EmailRequest;
import ru.urfu.cake.shop.notification.service.EmailService;

/**
 * Обрабатывает входящие email-уведомления из Kafka-потока.
 * <p>
 * Содержит только бизнес-логику: валидацию, отправку писем через {@link EmailService}
 * и запись метрик. Не содержит Kafka-специфичного кода. Обработка ошибок и маршрутизация
 * в DLQ делегированы {@link ru.urfu.cake.core.topology.StreamTopologyFactory}.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EmailNotificationHandler implements MessageHandler<EmailRequest> {

    private final EmailService emailService;
    private final MeterRegistry meterRegistry;

    @Override
    public void handle(EmailRequest request) throws Exception {
        log.info("Processing notification to={} type={}", request.getTo(), request.getType());
        Timer.Sample sample = Timer.start(meterRegistry);

        try {
            emailService.sendHtmlEmail(request);
            recordSuccess(sample);
        } catch (Exception ex) {
            recordFailure(sample, ex);
            throw ex;
        }
    }

    private void recordSuccess(Timer.Sample sample) {
        sample.stop(emailTimer("success"));
        meterRegistry.counter("notification.email.sent", "status", "success").increment();
    }

    private void recordFailure(Timer.Sample sample, Exception ex) {
        sample.stop(emailTimer("error"));
        meterRegistry.counter("notification.email.sent", "status", "failed").increment();
        log.error("Failed to process notification: {}", ex.getMessage(), ex);
    }

    private Timer emailTimer(String status) {
        return meterRegistry.timer("notification.email.duration", "status", status);
    }
}
