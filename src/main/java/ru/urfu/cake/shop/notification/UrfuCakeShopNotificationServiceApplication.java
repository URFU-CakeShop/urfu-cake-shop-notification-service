package ru.urfu.cake.shop.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@org.springframework.kafka.annotation.EnableKafkaStreams
public class UrfuCakeShopNotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UrfuCakeShopNotificationServiceApplication.class, args);
    }

}
