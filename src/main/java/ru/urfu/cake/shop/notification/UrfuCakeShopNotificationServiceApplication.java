package ru.urfu.cake.shop.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.kafka.annotation.EnableKafkaStreams;

@SpringBootApplication
@ComponentScan(basePackages = {
        "ru.urfu.cake.shop.notification",
        "ru.urfu.cake"
})
public class UrfuCakeShopNotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UrfuCakeShopNotificationServiceApplication.class, args);
    }
}