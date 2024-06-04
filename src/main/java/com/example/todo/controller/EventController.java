package com.example.todo.controller;

import com.example.todo.dto.ResponseDTO;
import com.example.todo.dto.EventDTO;
import com.example.todo.model.EventEntity;
import com.example.todo.service.EventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("event")
public class EventController {

    @Autowired
    private EventService service;

    @PostMapping
    public ResponseEntity<?> createEvent(@AuthenticationPrincipal String userId, @RequestBody EventDTO dto) {
        try {
            log.info("Log:createEvent entrance");

            EventEntity entity = EventDTO.eventEntity(dto);
            log.info("Log:dto => entity ok!");
            entity.setId(null);
            entity.setUserId(userId);

            List<EventEntity> entities = service.create(entity);
            log.info("Log:service.create ok!");

            List<EventDTO> dtos = entities.stream().map(EventDTO::new).collect(Collectors.toList());
            log.info("Log:entities => dtos ok!");

            ResponseDTO<EventDTO> response = ResponseDTO.<EventDTO>builder().data(dtos).build();
            log.info("Log:responsedto ok!");

            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            String error = e.getMessage();
            ResponseDTO<EventDTO> response = ResponseDTO.<EventDTO>builder().error(error).build();
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping
    public ResponseEntity<?> retrieveEvent(@AuthenticationPrincipal String userId) {
        List<EventEntity> entities = service.retrieve(userId);
        List<EventDTO> dtos = entities.stream().map(EventDTO::new).collect(Collectors.toList());

        ResponseDTO<EventDTO> response = ResponseDTO.<EventDTO>builder().data(dtos).build();

        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/{date}")
    public ResponseEntity<?> retrieveEventByDate(@AuthenticationPrincipal String userId, @PathVariable("date") LocalDate date) {
        List<EventEntity> entities = service.retrieveByDate(userId, date);
        List<EventDTO> dtos = entities.stream().map(EventDTO::new).collect(Collectors.toList());

        ResponseDTO<EventDTO> response = ResponseDTO.<EventDTO>builder().data(dtos).build();

        return ResponseEntity.ok().body(response);
    }

    @PutMapping
    public ResponseEntity<?> updateEvent(@AuthenticationPrincipal String userId, @RequestBody EventDTO dto) {
        try {
            EventEntity entity = EventDTO.eventEntity(dto);
            entity.setUserId(userId);
            List<EventEntity> entities = service.update(entity);
            List<EventDTO> dtos = entities.stream().map(EventDTO::new).collect(Collectors.toList());

            ResponseDTO<EventDTO> response = ResponseDTO.<EventDTO>builder().data(dtos).build();

            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            String error = e.getMessage();
            ResponseDTO<EventDTO> response = ResponseDTO.<EventDTO>builder().error(error).build();
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping
    public ResponseEntity<?> deleteEvent(@AuthenticationPrincipal String userId, @RequestBody EventDTO dto) {
        try {
            EventEntity entity = EventDTO.eventEntity(dto);
            entity.setUserId(userId);

            List<EventEntity> entities = service.delete(entity);
            List<EventDTO> dtos = entities.stream().map(EventDTO::new).collect(Collectors.toList());

            ResponseDTO<EventDTO> response = ResponseDTO.<EventDTO>builder().data(dtos).build();
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            String error = e.getMessage();
            ResponseDTO<EventDTO> response = ResponseDTO.<EventDTO>builder().error(error).build();
            return ResponseEntity.badRequest().body(response);
        }
    }
}
