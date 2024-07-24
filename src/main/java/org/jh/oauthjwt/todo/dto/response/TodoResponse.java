package org.jh.oauthjwt.todo.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jh.oauthjwt.todo.domain.Todo;
import org.jh.oauthjwt.todo.domain.type.CategoryType;
import org.jh.oauthjwt.todo.domain.type.PriorityType;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class TodoResponse {

    private final Long id;
    private final String title;
    private final String body;
    private final boolean isCompleted;
    private final LocalDateTime dueDate;
    private final PriorityType priority;
    private final boolean notified;
    private final CategoryType category;

    public static TodoResponse of(final Todo todo) {
        return new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getBody(),
                todo.isCompleted(),
                todo.getDueDate(),
                todo.getPriority(),
                todo.isNotified(),
                todo.getCategory()
        );
    }
}
