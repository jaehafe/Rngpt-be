package org.jh.oauthjwt.todo.presentation;

import jakarta.validation.Valid;
import org.jh.oauthjwt.dto.CustomUserDetails;
import org.jh.oauthjwt.jwt.JWTUtil;
import org.jh.oauthjwt.todo.dto.request.TodoCompletionRequest;
import org.jh.oauthjwt.todo.dto.request.CreateTodoRequest;
import org.jh.oauthjwt.todo.dto.request.UpdateTodoRequest;
import org.jh.oauthjwt.todo.dto.response.TodoResponse;
import org.jh.oauthjwt.todo.service.TodoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TodoController {

    private final TodoService todoService;

    private static final Logger logger = LoggerFactory.getLogger(TodoController.class);

    public TodoController(final TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping("/todo/{id}")
    public ResponseEntity<TodoResponse> getTodo(@PathVariable final Long id) {
        final TodoResponse todoResponse = todoService.getTodo(id);
        return ResponseEntity.ok(todoResponse);
    }

    @GetMapping("/todos")
    public ResponseEntity<Page<TodoResponse>> getTodos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            Authentication authentication
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));

        Page<TodoResponse> todoResponses = todoService.getAllTodos(pageable, authentication);
        return ResponseEntity.ok(todoResponses);
    }

    @PostMapping("/todos")
    public ResponseEntity<Void> createTodo(@RequestBody @Valid final CreateTodoRequest todoRequest,  Authentication authentication) {

            todoService.createTodo(todoRequest, authentication);
            return ResponseEntity.ok().build();
    }

    @PostMapping("/todos/{id}")
    public ResponseEntity<Void> updateTodo(@PathVariable @Valid final Long id, @RequestBody final UpdateTodoRequest updateRequest, Authentication authentication) {
        todoService.updateTodo(id, updateRequest, authentication);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/todos/{id}/completion")
    public ResponseEntity<Void> updateTodoCompletion(@PathVariable Long id, @RequestBody @Valid TodoCompletionRequest request, Authentication authentication) {
        todoService.updateTodoCompletion(id, request.isCompleted(), authentication);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/todo/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable final Long id, Authentication authentication) {
        todoService.deleteTodo(id, authentication);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<TodoResponse>> getTodosByCategory(@PathVariable final String category, Authentication authentication) {
        final List<TodoResponse> todoResponses = todoService.getTodosByCategory(category, authentication);
        return ResponseEntity.ok(todoResponses);
    }
}