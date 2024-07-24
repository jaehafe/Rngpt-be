package org.jh.oauthjwt.todo.service;

import lombok.RequiredArgsConstructor;
import org.jh.oauthjwt.global.exception.InvalidCategoryException;
import org.jh.oauthjwt.todo.domain.Todo;
import org.jh.oauthjwt.todo.domain.repository.TodoRepository;
import org.jh.oauthjwt.todo.domain.type.CategoryType;
import org.jh.oauthjwt.todo.dto.request.CreateTodoRequest;
import org.jh.oauthjwt.todo.dto.request.UpdateTodoRequest;
import org.jh.oauthjwt.todo.dto.response.TodoResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TodoService {

    private final TodoRepository todoRepository;

    private static final Logger logger = LoggerFactory.getLogger(TodoService.class);

    // todo 가져오기
    @Transactional(readOnly = true)
    public TodoResponse getTodo(Long id) {
        final Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("할 일이 없습니다."));
        return TodoResponse.of(todo);
    }

    // pagination
    @Transactional(readOnly = true)
    public Page<TodoResponse> getAllTodos(Pageable pageable) {
        Page<Todo> todoPage = todoRepository.findAll(pageable);
        return todoPage.map(TodoResponse::of);
    }

    // todo 생성
    public void createTodo(final CreateTodoRequest todoRequest) {
        validateTodoRequest(todoRequest);
        todoRepository.save(Todo.of(todoRequest));
    }

    private void validateTodoRequest(CreateTodoRequest todoRequest) {
        if (todoRequest.getTitle() == null || todoRequest.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Todo 제목은 비어있을 수 없습니다.");
        }
    }

    // todo update
    public void updateTodo(final Long id, final UpdateTodoRequest updateRequest) {
        final Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 id의 todo가 없습니다. id: " + id));
        todo.update(updateRequest);
    }

    // todo 완료
    @Transactional
    public void updateTodoCompletion(Long id, boolean isCompleted) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Todo를 찾을 수 없습니다. ID: " + id));
        todo.setCompleted(isCompleted);
    }

    // todo 삭제
    @Transactional
    public void deleteTodo(final Long id) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("삭제할 Todo를 찾을 수 없습니다. ID: " + id));
        todoRepository.delete(todo);
    }

    @Transactional(readOnly = true)
    public List<TodoResponse> getTodosByCategory(String category) {

        CategoryType categoryType;
        try {
            categoryType = CategoryType.valueOf(category.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidCategoryException("유효하지 않은 카테고리입니다: " + category);
        }

        List<Todo> todos = todoRepository.findByCategory(categoryType);

        if (todos.isEmpty()) {
            logger.info("카테고리 '{}' 에 해당하는 할 일이 없습니다.", category);
            // 여기서 빈 리스트를 반환하거나, 특별한 응답을 생성할 수 있습니다.
        }

        return todos.stream()
                .map(TodoResponse::of)
                .toList();
    }
}
