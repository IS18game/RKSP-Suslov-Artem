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

    @PostConstruct
    public void initClickHouseTable() {
        try {
            String createTableSql = "CREATE TABLE IF NOT EXISTS `агрегаты_событий_заказов` (" +
                    "`дата_и_время_записи` DateTime, " +
                    "`количество_записей` UInt64" +
                    ") ENGINE = MergeTree() ORDER BY `дата_и_время_записи`";
            
            String baseUrl = clickHouseUrl.replace("jdbc:clickhouse://", "http://")
                    .replace("/default", "")
                    .replace("?use_server_time_zone=false", "");
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            
            HttpEntity<String> request = new HttpEntity<>(createTableSql, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(
                    baseUrl + "/?query=" + URLEncoder.encode(createTableSql, StandardCharsets.UTF_8), 
                    null, 
                    String.class);
            
            System.out.println("Таблица агрегаты_событий_заказов создана/проверена в ClickHouse");
        } catch (Exception e) {
            System.err.println("Ошибка при создании таблицы в ClickHouse: " + e.getMessage());
            e.printStackTrace();
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
        try {
            String now = LocalDateTime.now().format(FORMATTER);
            String sql = String.format("INSERT INTO `агрегаты_событий_заказов` (`дата_и_время_записи`, `количество_записей`) VALUES ('%s', %d)", 
                    now, count);
            
            String baseUrl = clickHouseUrl.replace("jdbc:clickhouse://", "http://")
                    .replace("/default", "")
                    .replace("?use_server_time_zone=false", "");
            String encodedQuery = URLEncoder.encode(sql, StandardCharsets.UTF_8);
            
            restTemplate.postForObject(baseUrl + "/?query=" + encodedQuery, null, String.class);
            System.out.println("Количество записей (" + count + ") сохранено в ClickHouse");
        } catch (Exception e) {
            System.err.println("Ошибка при сохранении в ClickHouse: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
