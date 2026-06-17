package ru.urfu.cake.shop.notification.topology;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TopologyTestDriver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.urfu.cake.core.dlq.DeadLetterPublisher;
import ru.urfu.cake.core.serde.JsonSerde;
import ru.urfu.cake.core.topology.StreamTopologyFactory;
import ru.urfu.cake.shop.notification.dto.Request.EmailRequest;
import ru.urfu.cake.shop.notification.handler.EmailNotificationHandler;
import ru.urfu.cake.shop.notification.service.EmailService;

import java.util.Properties;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class NotificationStreamTopologyIntegrationTest {

    private static final String SOURCE_TOPIC = "notifications-topic";

    private final EmailService emailService = mock();
    private final DeadLetterPublisher deadLetterPublisher = mock();
    private final MeterRegistry meterRegistry = new SimpleMeterRegistry();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final StreamTopologyFactory topologyFactory =
            new StreamTopologyFactory(deadLetterPublisher, objectMapper);
    private final EmailNotificationHandler handler =
            new EmailNotificationHandler(emailService, meterRegistry);

    private TopologyTestDriver driver;

    @BeforeEach
    void setUp() {
        var builder = new StreamsBuilder();
        var serde = new JsonSerde<>(EmailRequest.class);
        topologyFactory.buildStream(builder, SOURCE_TOPIC, serde, handler);

        var props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "notification-test");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:1234");
        driver = new TopologyTestDriver(builder.build(), props);
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.close();
        }
    }

    @Test
    void shouldSendEmailForValidMessage() throws Exception {
        var request = new EmailRequest();
        request.setTo("user@example.com");
        request.setSubject("Test Subject");
        request.setHtmlContent("<html><body>Test</body></html>");

        inputTopic().pipeInput("key1", request);

        verify(emailService).sendHtmlEmail(request);
        verify(deadLetterPublisher, never()).publish(anyString(), anyString(), anyString(), any(), any());
    }

    @Test
    void shouldSendToDlqOnEmailFailure() throws Exception {
        doThrow(new RuntimeException("SMTP error"))
                .when(emailService).sendHtmlEmail(any());

        var request = new EmailRequest();
        request.setTo("user@example.com");
        request.setSubject("Test Subject");
        request.setHtmlContent("<html><body>Test</body></html>");

        inputTopic().pipeInput("key1", request);

        verify(deadLetterPublisher).publish(
                eq(SOURCE_TOPIC),
                eq("key1"),
                anyString(),
                eq(null),
                any(RuntimeException.class)
        );
    }

    @Test
    void shouldFilterNullMessage() {
        inputTopic().pipeInput("null-key", (EmailRequest) null);

        verifyNoInteractions(emailService);
        verifyNoInteractions(deadLetterPublisher);
    }

    @Test
    void shouldRecordSuccessMetrics() throws Exception {
        var request = new EmailRequest();
        request.setTo("user@example.com");
        request.setSubject("Test");

        inputTopic().pipeInput("key1", request);

        verify(emailService).sendHtmlEmail(request);
        var counter = meterRegistry.counter("notification.email.sent", "status", "success");
        var failedCounter = meterRegistry.counter("notification.email.sent", "status", "failed");
        var duration = meterRegistry.timer("notification.email.duration", "status", "success");

        org.assertj.core.api.Assertions.assertThat(counter.count()).isPositive();
        org.assertj.core.api.Assertions.assertThat(failedCounter.count()).isZero();
        org.assertj.core.api.Assertions.assertThat(duration.totalTime(java.util.concurrent.TimeUnit.NANOSECONDS)).isPositive();
    }

    private TestInputTopic<String, EmailRequest> inputTopic() {
        var serde = new JsonSerde<>(EmailRequest.class);
        return driver.createInputTopic(
                SOURCE_TOPIC,
                Serdes.String().serializer(),
                serde.serializer()
        );
    }
}
