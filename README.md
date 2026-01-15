# Экзаменационная работа - Суслов Артем

Проект состоит из двух микросервисов на Spring Boot 3.2.0 (Java 17):

- **ingest-service** (порт 8080): Принимает события через REST API и отправляет их в RabbitMQ
- **processor-service** (порт 8081): Обрабатывает события из RabbitMQ, сохраняет в PostgreSQL и агрегирует данные в ClickHouse

## Запуск

### 1. Запуск инфраструктуры

```powershell
cd processor-service
docker-compose up -d
```

**Проверка статуса:**
```powershell
docker ps
```

Должны быть запущены: PostgreSQL (5432), ClickHouse (8123, 9000), RabbitMQ (5672, 15672)

### 2. Запуск сервисов

**Ingest Service (терминал 1):**
```powershell
cd ingest-service
mvn spring-boot:run
```

Swagger UI: http://localhost:8080/swagger-ui/index.html

**Processor Service (терминал 2):**
```powershell
cd processor-service
mvn spring-boot:run
```

Swagger UI: http://localhost:8081/swagger-ui/index.html

## Технологии

- Spring Boot 3.2.0
- Java 17
- Maven
- PostgreSQL
- ClickHouse
- RabbitMQ
- Docker & Docker Compose
- Swagger/OpenAPI

## Статус проверки

Все компоненты протестированы и работают:
-  Ingest-service принимает события и отправляет в RabbitMQ
- Processor-service обрабатывает события из RabbitMQ
-  Данные сохраняются в PostgreSQL
-  ClickHouse - не успел проверить