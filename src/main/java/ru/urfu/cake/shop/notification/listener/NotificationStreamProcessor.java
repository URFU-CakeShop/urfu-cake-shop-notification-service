package ru.urfu.cake.shop.notification.listener;


import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.urfu.cake.dlq.DeadLetterPublisher;
import ru.urfu.cake.serde.JsonSerde;
import ru.urfu.cake.shop.notification.dto.EmailRequest;
import ru.urfu.cake.shop.notification.service.EmailService;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationStreamProcessor {

    private static final String SOURCE_TOPIC = "notifications-topic";

    private  final EmailService emailService;
    private final DeadLetterPublisher deadLetterPublisher;
    private final MeterRegistry meterRegistry;

    @Autowired
    public void buildPipeline(StreamsBuilder builder) {
        JsonSerde<EmailRequest> emailRequestSerde = new JsonSerde<>(EmailRequest.class);

        builder
                .stream(SOURCE_TOPIC, Consumed.with(Serdes.String(), emailRequestSerde))
                .filter((key, request) -> {
                    if (request == null
                            || request.getTo() == null || request.getTo().isBlank()
                            || request.getSubject() == null
                            || request.getHtmlContent() == null) {
                        log.warn("Получено невалидное сообщение key={}, пропускаем", key);
                        return false;
                    }
                    return true;
                })
                .foreach((key, request) -> {
                    log.info("Обработка уведомления key={} to={}", key, request.getTo());
                    Timer.Sample sample = Timer.start(meterRegistry);
                    try {
                        emailService.sendHtmlEmail(request);
                        sample.stop(meterRegistry.timer("notification.email.duration",
                                "status", "success"));
                        meterRegistry.counter("notification.email.sent",
                                "status", "success").increment();
                        log.info("Письмо успешно отправлено to={}", request.getTo());
                    } catch (Exception e) {
                        sample.stop(meterRegistry.timer("notification.email.duration",
                                "status", "error"));
                        meterRegistry.counter("notification.email.sent",
                                "status", "failed").increment();
                        log.error("Ошибка отправки to={}: {}", request.getTo(), e.getMessage(), e);
                        deadLetterPublisher.publish(
                                SOURCE_TOPIC, key,
                                request.getTo(),
                                null, e
                        );
                    }
                });
    }
}
