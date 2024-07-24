package org.jh.oauthjwt.todo.service;

import lombok.RequiredArgsConstructor;
import org.jh.oauthjwt.dto.CustomUserDetails;
import org.jh.oauthjwt.entity.UserEntity;
import org.jh.oauthjwt.global.exception.InvalidCategoryException;
import org.jh.oauthjwt.global.exception.UnauthorizedException;
import org.jh.oauthjwt.repository.UserRepository;
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
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TodoService {

    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

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
    public Page<TodoResponse> getAllTodos(Pageable pageable, Authentication authentication) {

        UserEntity user = getAuthenticatedUser(authentication);
        Page<Todo> todoPage = todoRepository.findByCreatorEmail(user.getEmail(), pageable);
        return todoPage.map(TodoResponse::of);
    }

    // todo 생성
    public void createTodo(final CreateTodoRequest todoRequest, Authentication authentication) {

        UserEntity userEntity = getAuthenticatedUser(authentication);
        validateTodoRequest(todoRequest);
        Todo todo = Todo.of(todoRequest, userEntity);
        todoRepository.save(todo);
    }

    private void validateTodoRequest(CreateTodoRequest todoRequest) {
        if (todoRequest.getTitle() == null || todoRequest.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Todo 제목은 비어있을 수 없습니다.");
        }
    }

    // todo update
    public void updateTodo(final Long id, final UpdateTodoRequest updateRequest, Authentication authentication) {

        getAuthenticatedUser(authentication);
        final Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 id의 todo가 없습니다. id: " + id));
        todo.update(updateRequest);
    }

    // todo 완료
    @Transactional
    public void updateTodoCompletion(Long id, boolean isCompleted, Authentication authentication) {
        getAuthenticatedUser(authentication);
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Todo를 찾을 수 없습니다. ID: " + id));
        todo.setCompleted(isCompleted);
    }

    // todo 삭제
    @Transactional
    public void deleteTodo(final Long id, Authentication authentication) {
        getAuthenticatedUser(authentication);
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("삭제할 Todo를 찾을 수 없습니다. ID: " + id));
        todoRepository.delete(todo);
    }

    @Transactional(readOnly = true)
    public List<TodoResponse> getTodosByCategory(String category, Authentication authentication) {

        getAuthenticatedUser(authentication);
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

    private UserEntity getAuthenticatedUser(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userRepository.findByEmail(userDetails.getEmail())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
        }
        throw new UnauthorizedException("User not authenticated");
    }
}
