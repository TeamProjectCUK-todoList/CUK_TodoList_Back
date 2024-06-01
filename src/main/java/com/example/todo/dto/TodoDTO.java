package com.example.todo.dto;

import com.example.todo.model.TodoEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data   //setter getter 만들어줌
public class TodoDTO {
    private String id;
    private String title;
    private boolean done;

    public TodoDTO(final TodoEntity entitiy){
        this.id = entitiy.getId();
        this.title = entitiy.getTitle();
        this.done = entitiy.isDone();
    }
    public static TodoEntity todoEntity(final TodoDTO dto){
        return TodoEntity.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .done(dto.isDone()).build();
    }
}
