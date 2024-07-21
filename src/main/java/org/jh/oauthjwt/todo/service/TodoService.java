package org.jh.oauthjwt.todo.service;

import jakarta.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jh.oauthjwt.todo.domain.Todo;
import org.jh.oauthjwt.todo.domain.repository.TodoRepository;
import org.jh.oauthjwt.todo.dto.response.TodoResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class TodoService {

    private final TodoRepository todoRepository;

    // todo 전체 가져오기
    public List<TodoResponse> getAllTodos() {
        final List<Todo> todos = todoRepository.findAll();
        if (todos.isEmpty()) {
            return Collections.emptyList();
        }
        return todos.stream()
                .map(TodoResponse::of)
                .toList();
    }
}
