package ru.rksp.suslov.service;

import jakarta.annotation.PostConstruct;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.rksp.suslov.dto.EventDto;
import ru.rksp.suslov.entity.RawEvent;
import ru.rksp.suslov.repository.RawEventRepository;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class EventProcessorService {

    @Autowired
    private RawEventRepository rawEventRepository;

    @Value("${clickhouse.datasource.url}")
    private String clickHouseUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter CLICKHOUSE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @PostConstruct
    public void initClickHouseTable() {
        try {
            String baseUrl = "http://localhost:8123";
            String checkTableSql = "EXISTS TABLE `агрегаты_событий_заказов`";
            String encodedCheck = URLEncoder.encode(checkTableSql, StandardCharsets.UTF_8);
            
            try {
                String response = restTemplate.getForObject(baseUrl + "/?query=" + encodedCheck, String.class);
                if (response != null && response.trim().equals("1")) {
                    System.out.println("Таблица агрегаты_событий_заказов уже существует в ClickHouse");
                    return;
                }
            } catch (Exception e) {
            }
            
            System.out.println("Таблица агрегаты_событий_заказов должна быть создана вручную через http://localhost:8123/play");
            System.out.println("Используйте SQL: CREATE TABLE IF NOT EXISTS `агрегаты_событий_заказов` (`дата_и_время_записи` DateTime, `количество_записей` UInt64) ENGINE = MergeTree() ORDER BY `дата_и_время_записи`");
        } catch (Exception e) {
            System.err.println("Ошибка при проверке таблицы в ClickHouse: " + e.getMessage());
        }
    }

    @RabbitListener(queues = "events.raw")
    @Transactional
    public void processEvent(EventDto eventDto) {
        try {
            System.out.println("=== Начало обработки события ===");
            System.out.println("Получено событие из RabbitMQ:");
            System.out.println("  - Номер заказа: " + eventDto.getНомерЗаказа());
            System.out.println("  - Телефон: " + eventDto.getНомерТелефонаПокупателя());
            System.out.println("  - Описание: " + eventDto.getОписаниеЗаказа());
            System.out.println("  - Дата: " + eventDto.getДатаСобытия());
            
            RawEvent rawEvent = new RawEvent();
            rawEvent.setНомерЗаказа(eventDto.getНомерЗаказа());
            rawEvent.setНомерТелефонаПокупателя(eventDto.getНомерТелефонаПокупателя());
            rawEvent.setОписаниеЗаказа(eventDto.getОписаниеЗаказа());
            rawEvent.setДатаСобытия(eventDto.getДатаСобытия());
            
            System.out.println("Сохранение в PostgreSQL...");
            RawEvent saved = rawEventRepository.save(rawEvent);
            rawEventRepository.flush();
            
            System.out.println("✓ Событие успешно сохранено в PostgreSQL!");
            System.out.println("  - ID: " + saved.getИдентификатор());
            System.out.println("  - Номер заказа: " + saved.getНомерЗаказа());
            System.out.println("=== Конец обработки события ===");
        } catch (Exception e) {
            System.err.println("✗ ОШИБКА при обработке события!");
            System.err.println("Тип ошибки: " + e.getClass().getName());
            System.err.println("Сообщение: " + e.getMessage());
            System.err.println("Стек вызовов:");
            e.printStackTrace();
            throw e;
        }
    }

    public void saveCountToClickHouse(long count) {
        String sql = null;
        try {
            String now = LocalDateTime.now().format(CLICKHOUSE_FORMATTER);
            sql = String.format("INSERT INTO `агрегаты_событий_заказов` (`дата_и_время_записи`, `количество_записей`) VALUES ('%s', %d)", 
                    now, count);
            
            String baseUrl = "http://localhost:8123";
            String encodedQuery = URLEncoder.encode(sql, StandardCharsets.UTF_8);
            String fullUrl = baseUrl + "/?query=" + encodedQuery;
            
            System.out.println("=== Сохранение в ClickHouse ===");
            System.out.println("Количество записей: " + count);
            System.out.println("SQL: " + sql);
            System.out.println("URL: " + fullUrl);
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "text/plain; charset=utf-8");
            HttpEntity<String> request = new HttpEntity<>(sql, headers);
            
            ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + "/", request, String.class);
            System.out.println("Статус ответа: " + response.getStatusCode());
            String responseBody = response.getBody();
            System.out.println("Тело ответа: " + (responseBody != null ? responseBody : "(пусто)"));
            
            if (response.getStatusCode().is2xxSuccessful()) {
                if (responseBody == null || responseBody.isEmpty() || responseBody.trim().equals("")) {
                    System.out.println("✓ Количество записей (" + count + ") успешно сохранено в ClickHouse");
                } else if (responseBody.contains("Exception") || responseBody.contains("Error") || responseBody.contains("Code:")) {
                    System.err.println("✗ ClickHouse вернул ошибку: " + responseBody);
                } else {
                    System.out.println("✓ Количество записей (" + count + ") успешно сохранено в ClickHouse");
                }
            } else {
                System.err.println("✗ Неожиданный статус ответа от ClickHouse: " + response.getStatusCode());
                System.err.println("Тело ответа: " + responseBody);
            }
        } catch (Exception e) {
            System.err.println("✗ ОШИБКА при сохранении в ClickHouse!");
            System.err.println("Тип ошибки: " + e.getClass().getName());
            System.err.println("Сообщение: " + e.getMessage());
            if (sql != null) {
                System.err.println("SQL запрос: " + sql);
            }
            e.printStackTrace();
        }
    }
}
