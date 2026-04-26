package ru.urfu.cake.shop.notification.listener;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.urfu.cake.serde.JsonSerde;
import ru.urfu.cake.shop.notification.dto.EmailRequest;
import ru.urfu.cake.shop.notification.service.EmailService;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationStreamProcessor {

    private  final EmailService emailService;

    @Autowired
    public void buildPipeline(StreamsBuilder builder) {
        JsonSerde<EmailRequest> emailRequestSerde = new JsonSerde<>(EmailRequest.class);

        builder
                .stream("notifications-topic", Consumed.with(Serdes.String(), emailRequestSerde))

                .filter((key, request) -> request != null && request.getTo() != null && !request.getTo().isBlank())

                .foreach((key, request) -> {
                    log.info("Получено событие для отправки письма на email: {}", request.getTo());
                    try {
                        emailService.sendHtmlEmail(request);
                        log.info("Письмо успешно отправлено на email: {}", request.getTo());
                    } catch (Exception e) {
                        log.error("Критическая ошибка при отправке письма на {}: {}", request.getTo(), e.getMessage(), e);
                    }
                });
    }
}
