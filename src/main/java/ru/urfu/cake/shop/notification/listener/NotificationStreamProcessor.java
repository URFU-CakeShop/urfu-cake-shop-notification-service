package ru.urfu.cake.shop.notification.listener;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.urfu.cake.dlq.DeadLetterPublisher;
import ru.urfu.cake.serde.JsonSerde;
import ru.urfu.cake.shop.notification.dto.Request.EmailRequest;
import ru.urfu.cake.shop.notification.service.EmailService;

/**
 * Kafka Streams processor for incoming notification messages.
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>Filter invalid messages</li>
 *   <li>Delegate sending to {@link EmailService}</li>
 *   <li>Route failures to DLQ via {@link DeadLetterPublisher}</li>
 *   <li>Record metrics</li>
 * </ul>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationStreamProcessor {

    static final String SOURCE_TOPIC = "notifications-topic";
    static final String STATUS_SENT = "SENT";
    static final String STATUS_FAILED = "FAILED";

    private final EmailService emailService;
    private final DeadLetterPublisher deadLetterPublisher;
    private final MeterRegistry meterRegistry;

    @Autowired
    public void buildPipeline(StreamsBuilder builder) {
        JsonSerde<EmailRequest> serde = new JsonSerde<>(EmailRequest.class);

        KStream<String, EmailRequest> stream = builder
                .stream(SOURCE_TOPIC, Consumed.with(Serdes.String(), serde));

        stream
                .filter(this::isValid)
                .foreach(this::processMessage);
    }

    boolean isValid(String key, EmailRequest request) {
        if (request == null) {
            log.warn("Received null message, key={} — skipping", key);
            return false;
        }
        if (request.getTo() == null || request.getTo().isBlank()) {
            log.warn("Missing recipient in message key={} — skipping", key);
            return false;
        }
        return true;
    }

    void processMessage(String key, EmailRequest request) {
        log.info("Processing notification key={} to={} type={}", key, request.getTo(), request.getType());
        Timer.Sample sample = Timer.start(meterRegistry);

        try {
            emailService.sendHtmlEmail(request);
            recordSuccess(sample);
        } catch (Exception ex) {
            recordFailure(sample, ex);
            deadLetterPublisher.publish(SOURCE_TOPIC, key, request.getTo(), null, ex);
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