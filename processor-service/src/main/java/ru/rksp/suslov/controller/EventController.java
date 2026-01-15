package ru.rksp.suslov.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.rksp.suslov.repository.RawEventRepository;
import ru.rksp.suslov.service.EventProcessorService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Events API", description = "API для работы с событиями")
public class EventController {

    @Autowired
    private RawEventRepository rawEventRepository;

    @Autowired
    private EventProcessorService eventProcessorService;

    @PostMapping("/events/count")
    @Operation(summary = "Получить количество событий", description = "Получает количество записей из PostgreSQL и сохраняет в ClickHouse")
    public ResponseEntity<Map<String, Object>> getEventsCount() {
        long count = rawEventRepository.count();
        eventProcessorService.saveCountToClickHouse(count);
        
        Map<String, Object> response = new HashMap<>();
        response.put("count", count);
        response.put("message", "Count saved to ClickHouse");
        return ResponseEntity.ok(response);
    }
}
