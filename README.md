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

### Быстрая проверка:

1. **Запустите инфраструктуру и сервисы** (см. раздел "Запуск")

2. **Отправьте тестовое событие:**
   - Откройте http://localhost:8080/swagger-ui/index.html
   - POST `/api/v1/events` → Try it out
   ```json
   {
     "идентификатор": "test-001",
     "номерЗаказа": "ORD-001",
     "номерТелефонаПокупателя": "+79991234567",
     "описаниеЗаказа": "Тестовый заказ",
     "датаСобытия": "2026-01-15T22:00:00"
   }
   ```
   - Нажмите Execute
   - Должен вернуться ответ: `"Event sent to RabbitMQ"`
   
   **Важно:** Используйте точные имена полей с заглавными буквами: `номерЗаказа`, `номерТелефонаПокупателя`, `описаниеЗаказа`, `датаСобытия`

3. **Проверьте логи processor-service:**
   - Должны появиться сообщения: `=== Начало обработки события ===` и `✓ Событие успешно сохранено в PostgreSQL!`

4. **Проверьте данные в PostgreSQL:**
   - Откройте DBeaver → `SELECT * FROM сырые_события_заказов;`
   - Должна появиться запись

5. **Проверьте RabbitMQ:**
   - Откройте http://localhost:15672 → Queues → `events.raw`
   - Consumers должно быть 1, Ready должно быть 0

6. **Сохраните количество в ClickHouse:**
   - Откройте http://localhost:8081/swagger-ui/index.html
   - POST `/api/v1/events/count` → Execute
   - Должен вернуться ответ с количеством записей

7. **Проверьте ClickHouse:**
   - Откройте http://localhost:8123/play
   - Выполните: `SELECT * FROM `агрегаты_событий_заказов` ORDER BY `дата_и_время_записи` DESC;`

## Статус проверки

Все компоненты протестированы и работают:
-  Ingest-service принимает события и отправляет в RabbitMQ
- Processor-service обрабатывает события из RabbitMQ
-  Данные сохраняются в PostgreSQL
-  ClickHouse - не успел проверить
