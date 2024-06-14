package com.example.todo.service;

import com.example.todo.model.EventEntity;
import com.example.todo.model.TodoEntity;
import com.example.todo.persistence.EventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class EventService {
    @Autowired
    private EventRepository repository;

    public List<EventEntity> create(final EventEntity entity) {
        // Validations
        validate(entity);
        repository.save(entity);
        return repository.findByUserId(entity.getUserId());
    }

    public List<EventEntity> retrieve(final String userId) {
        return repository.findByUserId(userId);
    }

    public List<EventEntity> retrieveByDate(final String userId, final LocalDate date) {
        return repository.findByUserIdAndDate(userId, date);
    }

    public List<EventEntity> update(final EventEntity entity) {
        // Validations
        validate(entity);
        if (repository.existsById(entity.getId())) {
            repository.save(entity);
        } else {
            throw new RuntimeException("Unknown id");
        }

        return repository.findByUserId(entity.getUserId());
    }

    public List<EventEntity> delete(final EventEntity entity) {
        if (repository.existsById(entity.getId())) {
            repository.deleteById(entity.getId());
        } else {
            throw new RuntimeException("id does not exist");
        }
        return repository.findByUserId(entity.getUserId());
    }

    // 날짜별 일괄 삭제
    public void deleteByDate(final String userId, final LocalDate date) {
        List<EventEntity> events = repository.findByUserIdAndDate(userId, date);
        repository.deleteAll(events);
    }

    public void validate(final EventEntity entity) {
        if (entity == null) {
            log.warn("Entity cannot be null");
            throw new RuntimeException("Entity cannot be null");
        }
        if (entity.getUserId() == null) {
            log.warn("Unknown user.");
            throw new RuntimeException("Unknown user.");
        }
    }
}
