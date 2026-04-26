FROM gradle:9.3.1-jdk17-corretto AS builder
WORKDIR /app

COPY cake-core /app/cake-core
WORKDIR /app/cake-core
RUN gradle :connection-starters-core:publishToMavenLocal --no-daemon

WORKDIR /app/urfu-cake-shop-notification-service
COPY urfu-cake-shop-notification-service/build.gradle .
COPY urfu-cake-shop-notification-service/settings.gradle .
COPY urfu-cake-shop-notification-service/src src

RUN gradle bootJar --no-daemon

FROM amazoncorretto:17-alpine
WORKDIR /app
COPY --from=builder /app/urfu-cake-shop-notification-service/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]