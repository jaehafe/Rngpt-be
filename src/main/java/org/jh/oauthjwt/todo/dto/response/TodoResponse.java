package org.jh.oauthjwt.todo.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jh.oauthjwt.todo.domain.Todo;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class TodoResponse {

    private final Long id;
    private final String title;

    public static TodoResponse of(final Todo todo) {
        return new TodoResponse(todo.getId(), todo.getTitle());
    }
}
