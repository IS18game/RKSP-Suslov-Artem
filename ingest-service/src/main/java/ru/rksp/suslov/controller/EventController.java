package ru.rksp.suslov.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.rksp.suslov.dto.EventDto;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Events API", description = "API для работы с событиями")
public class EventController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostMapping("/events")
    @Operation(summary = "Создать событие", description = "Отправляет событие в RabbitMQ очередь events.raw")
    public ResponseEntity<String> createEvent(@RequestBody EventDto event) {
        try {
            if (event.getНомерЗаказа() == null || event.getНомерЗаказа().isEmpty()) {
                return ResponseEntity.badRequest().body("Field 'номерЗаказа' is required");
            }
            
            rabbitTemplate.convertAndSend("", "events.raw", event);
            return ResponseEntity.ok("Event sent to RabbitMQ");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error sending event to RabbitMQ: " + e.getMessage());
        }
    }
}
