package org.jh.oauthjwt.todo.service;

import jakarta.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jh.oauthjwt.todo.domain.Todo;
import org.jh.oauthjwt.todo.domain.repository.TodoRepository;
import org.jh.oauthjwt.todo.dto.request.TodoRequest;
import org.jh.oauthjwt.todo.dto.response.TodoResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class TodoService {

    private final TodoRepository todoRepository;

    private static final Logger logger = LoggerFactory.getLogger(TodoService.class);

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

    // todo 생성
    public void createTodo(final TodoRequest todoRequest) {
        todoRepository.save(Todo.of(todoRequest));
    }

    // todo update
    public void updateTodo(final Long id, final TodoRequest todoRequest) {
        final Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 id의 todo가 없습니다. id: " + id));
        todo.update(todoRequest);
    }

    // todo 완료
    @Transactional
    public void updateTodoCompletion(Long id, boolean isCompleted) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found"));
        todo.setCompleted(isCompleted);
    }

    // todo 삭제
    @Transactional
    public void deleteTodo(final Long id) {
        todoRepository.deleteById(id);
    }
}
