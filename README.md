<div align="center">

# 🍰 Cake Shop — Notification Service

**Микросервис email-уведомлений для интернет-кондитерской**

[![Java](https://img.shields.io/badge/Java-17-orange?style=flat-square&logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.5-brightgreen?style=flat-square&logo=spring)](https://spring.io/projects/spring-boot)
[![Kafka Streams](https://img.shields.io/badge/Kafka%20Streams-enabled-black?style=flat-square&logo=apachekafka)](https://kafka.apache.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue?style=flat-square&logo=postgresql)](https://www.postgresql.org/)
[![License](https://img.shields.io/badge/version-1.2.1-pink?style=flat-square)](CHANGELOG.md)

</div>

---

## 📖 Содержание

- [О сервисе](#-о-сервисе)
- [Архитектура](#-архитектура)
- [Возможности](#-возможности)
- [Технологии](#-технологии)
- [Быстрый старт](#-быстрый-старт)
- [Конфигурация](#-конфигурация)
- [Kafka: основной способ получения сообщений](#-kafka-основной-способ-получения-сообщений)
- [REST API — Swagger](#-rest-api--swagger)
- [Шаблоны уведомлений](#-шаблоны-уведомлений)
- [Мониторинг](#-мониторинг)

---

## 🎯 О сервисе

**Notification Service** — это микросервис, отвечающий за отправку HTML email-уведомлений пользователям кондитерской Cake Shop. Сервис интегрируется в общую микросервисную экосистему через **Apache Kafka**, принимает события от других сервисов и доставляет красивые письма конечным пользователям.

> **Основной канал получения задач** — Kafka Streams. REST API доступен для тестирования и управления шаблонами.

---

## 🏗 Архитектура

```
┌─────────────────────────────────────────────────────────────────────┐
│                        Notification Service                         │
│                                                                     │
│  ┌─────────────────┐    ┌──────────────────┐   ┌────────────────┐   │
│  │  Kafka Streams  │──-▶│  EmailService    │──▶│  SMTP (Gmail)  │   │
│  │  (основной      │    │                  │   │                │   │
│  │   поток)        │    │  ContentResolver │   └────────────────┘   │
│  └─────────────────┘    │                  │                        │
│                         │  TemplateService │──▶┌────────────────┐   │
│  ┌─────────────────┐    │  (Mustache)      │   │  PostgreSQL    │   │
│  │  REST API       │───▶│                  │   │  (шаблоны)     │   │
│  │  (тестирование  │    └──────────────────┘   └────────────────┘   │
│  │   + шаблоны)    │                                                │
│  └─────────────────┘    ┌──────────────────┐                        │
│                         │  DLQ Publisher   │──▶ notifications-      │
│                         │  (при ошибках)   │    topic.DLQ           │
│                         └──────────────────┘                        │
└─────────────────────────────────────────────────────────────────────┘
```

---

## ✨ Возможности

| Функция | Описание |
|---|---|
| 📨 **Kafka Streams** | Приём и обработка событий в реальном времени |
| 📧 **HTML Email** | Отправка красивых писем через SMTP |
| 🎨 **Шаблоны** | Управление HTML-шаблонами писем через API |
| 🔄 **Динамические переменные** | Подстановка данных в шаблоны (`{{orderId}}`, `{{name}}`) |
| ☠️ **Dead Letter Queue** | Автоматическая маршрутизация ошибочных сообщений в DLQ |
| 📊 **Метрики** | Prometheus + Grafana из коробки |
| 🧪 **Swagger UI** | Тестирование отправки и управление шаблонами |

---

## 🛠 Технологии

- **Java 17** + **Spring Boot 4.0.5**
- **Apache Kafka** + **Kafka Streams** — основной транспорт
- **Spring Data JPA** + **PostgreSQL 15** — хранение шаблонов
- **Liquibase** — миграции БД
- **Mustache (JMustache)** — движок шаблонизации
- **JavaMailSender** — SMTP транспорт
- **Spring Boot Actuator** + **Micrometer** + **Prometheus** — мониторинг
- **SpringDoc OpenAPI (Swagger)** — документация API
- **Docker Compose** — контейнеризация
- **Testcontainers** — интеграционные тесты

---

## 🚀 Быстрый старт

### Вариант 1 — Полный стек (Kafka + сервис + БД)

Если у вас нет работающего Kafka и других инфраструктурных сервисов — поднимите всё:

```bash
# Шаг 1: Создать внешнюю сеть (если ещё не создана)
docker network create cake-shop-network

# Шаг 2: Запустить Kafka, Zookeeper, Prometheus, Grafana
docker compose -f attached-applications.yml up -d

# Шаг 3: Запустить сам сервис + PostgreSQL
docker compose up -d
```

### Вариант 2 — Только сервис + БД (Kafka уже есть)

Если Kafka и другие сервисы уже запущены в вашей среде и notification service имеет к ним сетевой доступ — `attached-applications.yml` не нужен:

```bash
# Только сервис и его база данных
docker compose up -d
```

### Проверка работоспособности

После запуска откройте Swagger UI:

```
http://localhost:8080/swagger-ui.html
```

---

## ⚙️ Конфигурация

Все настройки сервиса передаются через переменные окружения (файл `config/notify.env`):

```env
# SMTP
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=your-email@gmail.com
SPRING_MAIL_PASSWORD=your-app-password

# База данных
DB_HOST=cake-shop-notification-db

# Kafka
KAFKA_BOOTSTRAP_SERVERS=kafka:9092
```

> ⚠️ Для Gmail используйте **App Password**, а не пароль от аккаунта. Включить в настройках Google → Безопасность → Двухэтапная аутентификация → Пароли приложений.

### Kafka топики

Автоматически создаются при старте `attached-applications.yml`:

| Топик | Назначение | Партиций |
|---|---|---|
| `notifications-topic` | Основной входящий поток | 3 |
| `notifications-topic.DLQ` | Dead Letter Queue для ошибок | 1 |

---

## 📨 Kafka: основной способ получения сообщений

Сервис читает топик `notifications-topic` через **Kafka Streams**. Каждое сообщение должно быть JSON-объектом формата `EmailRequest`.

### Формат сообщения

```json
{
  "to": "customer@example.com",
  "type": "ORDER_CONFIRMED",
  "payload": {
    "orderId": "12345",
    "name": "Иван Иванов",
    "totalAmount": "1500"
  }
}
```

**Поля:**

| Поле | Обязательно | Описание |
|---|---|---|
| `to` | ✅ | Email получателя |
| `type` | ⬜ | Ключ шаблона из БД (если задан — `subject` и `htmlContent` игнорируются) |
| `payload` | ⬜ | Переменные для подстановки в шаблон (`{{key}}`) |
| `subject` | ⬜ | Тема письма (только в Direct Mode, без `type`) |
| `htmlContent` | ⬜ | HTML-тело письма (только в Direct Mode, без `type`) |
| `userId` | ⬜ | ID пользователя (поле для будущего использования) |
| `status` | ⬜ | Статус уведомления (поле для будущего использования) |

### Режимы отправки

```
Сообщение получено
       │
       ▼
  Указан "type"? ──── Да ───▶ Загрузить шаблон из БД по ключу
       │                       Подставить payload → отправить
       │ Нет
       ▼
  Использовать "subject" + "htmlContent" напрямую → отправить
```

### Обработка ошибок

Если отправка письма не удалась — сообщение автоматически публикуется в `notifications-topic.DLQ` для дальнейшего анализа.

---

## 🧪 REST API — Swagger

Swagger UI: `http://localhost:8080/swagger-ui.html`

REST API сделан **для тестирования** и **управления шаблонами**. Для продакшн-нагрузки используйте Kafka.

### Отправка письма напрямую

```
POST /notification
```

**Direct Mode** (без шаблона):

```json
{
  "to": "test@example.com",
  "subject": "Тест!",
  "htmlContent": "<html><body><h1>Привет!</h1></body></html>"
}
```

**Template Mode** (с шаблоном из БД):

```json
{
  "to": "customer@example.com",
  "type": "WELCOME_NEW_CUSTOMER",
  "payload": {
    "firstName": "Мария",
    "promo": "CAKE15"
  }
}
```

---

## 🎨 Шаблоны уведомлений

Шаблоны хранятся в PostgreSQL и управляются через REST API (`/notification/templates`).

### Встроенные шаблоны (preloaded)

Три шаблона загружаются автоматически при первом запуске через Liquibase-миграцию:

| Ключ | Назначение | Переменные |
|---|---|---|
| `WELCOME_NEW_CUSTOMER` | Приветственное письмо новому клиенту | `{{firstName}}`, `{{promo}}` |
| `ORDER_CONFIRMED` | Подтверждение заказа | `{{orderId}}`, `{{name}}`, `{{totalAmount}}` |
| `ORDER_SHIPPID` | Уведомление об отправке | `{{firstName}}`, `{{orderId}}`, `{{trackingNumber}}` |

### API управления шаблонами

| Метод | Endpoint | Описание |
|---|---|---|
| `GET` | `/notification/templates` | Получить все шаблоны |
| `POST` | `/notification/templates` | Создать или обновить шаблон |
| `DELETE` | `/notification/templates/{key}` | Удалить шаблон по ключу |

### Создание своего шаблона

```json
POST /notification/templates
{
  "templateKey": "MY_CUSTOM_TEMPLATE",
  "subjectTemplate": "Привет, {{name}}! Новинка в меню 🎂",
  "bodyTemplate": "<html><body><h1>{{name}}, попробуй {{cake}}!</h1></body></html>"
}
```

Теперь можно отправить письмо через Kafka или REST:

```json
{
  "to": "fan@example.com",
  "type": "MY_CUSTOM_TEMPLATE",
  "payload": {
    "name": "Алексей",
    "cake": "малиновый тарт"
  }
}
```

### Синтаксис переменных (Mustache)

В шаблонах используется движок **Mustache** с двойными фигурными скобками:

```html
<h2>Привет, {{firstName}}!</h2>
<p>Ваш заказ №{{orderId}} на сумму {{totalAmount}} руб. подтверждён.</p>
```

Если переменная не передана в `payload` — вместо неё подставится `ДАННЫЕ ОТСУТСТВУЮТ`.

---

## 📊 Мониторинг

| Сервис | URL | Логин / Пароль |
|---|---|---|
| **Actuator / Health** | `http://localhost:8080/actuator/health` | — |
| **Prometheus метрики** | `http://localhost:8080/actuator/prometheus` | — |
| **Prometheus UI** | `http://localhost:9090` | — |
| **Grafana** | `http://localhost:3000` | `admin` / `admin` |

### Ключевые метрики

| Метрика | Описание |
|---|---|
| `notification.email.sent{status="success"}` | Счётчик успешно отправленных писем |
| `notification.email.sent{status="failed"}` | Счётчик неудачных попыток |
| `notification.email.duration` | Время обработки уведомления (с гистограммой) |
| `http.server.requests` | Стандартные HTTP метрики Spring |



<div align="center">

Сделано с любовью и щепоткой сахара 🍰  
**Cake Shop © 2026**

</div>