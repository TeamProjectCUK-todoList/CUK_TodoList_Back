package com.example.todo.persistence;

import com.example.todo.model.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, String> {
    @Query("select t from EventEntity t where t.userId = ?1")
    List<EventEntity> findByUserId(String userId);

    @Query("select t from EventEntity t where t.userId = ?1 and t.date = ?2")
    List<EventEntity> findByUserIdAndDate(String userId, LocalDate date);
}
