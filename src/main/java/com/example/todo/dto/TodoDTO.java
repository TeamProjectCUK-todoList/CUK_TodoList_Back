package com.example.todo.dto;

import com.example.todo.model.TodoEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data   //setter getter 만들어줌
public class TodoDTO {
    private String id;
    private String title;
    private boolean done;
    private LocalDate date; // 날짜 필드 추가

    public TodoDTO(final TodoEntity entity) {
        this.id = entity.getId();
        this.title = entity.getTitle();
        this.done = entity.isDone();
        this.date = entity.getDate(); // 날짜 필드 추가
    }

    public static TodoEntity todoEntity(final TodoDTO dto) {
        return TodoEntity.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .done(dto.isDone())
                .date(dto.getDate()) // 날짜 필드 추가
                .build();
    }
}
