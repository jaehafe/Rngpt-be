package org.jh.oauthjwt.todo.presentation;

import jakarta.validation.Valid;
import java.util.List;
import org.jh.oauthjwt.todo.dto.request.TodoCompletionRequest;
import org.jh.oauthjwt.todo.dto.request.TodoRequest;
import org.jh.oauthjwt.todo.dto.response.TodoResponse;
import org.jh.oauthjwt.todo.service.TodoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TodoController {

    private final TodoService todoService;

    private static final Logger logger = LoggerFactory.getLogger(TodoService.class);

    public TodoController(final TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping("/todo/{id}")
    public ResponseEntity<TodoResponse> getTodo(@PathVariable final Long id) {
        final TodoResponse todoResponse = todoService.getTodo(id);
        return ResponseEntity.ok(todoResponse);
    }

    @GetMapping("/todos")
    public ResponseEntity<List<TodoResponse>> getTodos() {
        final List<TodoResponse> todoResponses = todoService.getAllTodos();
        return ResponseEntity.ok(todoResponses);
    }

    @PostMapping("/todos")
    public ResponseEntity<Void> createTodo(@RequestBody final TodoRequest todoRequest) {
        todoService.createTodo(todoRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/todos/{id}")
    public ResponseEntity<Void> updateTodo(@PathVariable final Long id, @RequestBody final TodoRequest todoRequest) {
        todoService.updateTodo(id, todoRequest);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/todos/{id}/completion")
    public ResponseEntity<Void> updateTodoCompletion(@PathVariable Long id, @RequestBody @Valid TodoCompletionRequest request) {
        todoService.updateTodoCompletion(id, request.isCompleted());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/todo/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable final Long id) {
        todoService.deleteTodo(id);
        return ResponseEntity.ok().build();
    }
}