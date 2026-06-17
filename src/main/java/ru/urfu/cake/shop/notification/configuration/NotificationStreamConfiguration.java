package ru.urfu.cake.shop.notification.configuration;

import org.apache.kafka.streams.StreamsBuilder;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import ru.urfu.cake.core.serde.JsonSerde;
import ru.urfu.cake.core.topology.StreamTopologyFactory;
import ru.urfu.cake.shop.notification.dto.Request.EmailRequest;
import ru.urfu.cake.shop.notification.handler.EmailNotificationHandler;
/**
 * Собирает топологию Kafka Streams для сервиса уведомлений.
 * <p>
 * Это единственный класс в микросервисе, содержащий Kafka-специфичный код.
 * Он делегирует построение топологии {@link StreamTopologyFactory} и указывает
 * только имя исходного топика и serde для значения.
 */
@Configuration
@RequiredArgsConstructor
public class NotificationStreamConfiguration {
    private static final String SOURCE_TOPIC = "notifications-topic";

    private final StreamsBuilder streamsBuilder;
    private final StreamTopologyFactory topologyFactory;
    private final EmailNotificationHandler handler;

    @PostConstruct
    public void setupPipeline() {
        JsonSerde<EmailRequest> serde = new JsonSerde<>(EmailRequest.class);
        topologyFactory.buildStream(streamsBuilder, SOURCE_TOPIC, serde, handler);
    }
}
