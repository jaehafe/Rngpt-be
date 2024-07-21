package org.jh.oauthjwt.todo.presentation;

import java.util.List;
import org.jh.oauthjwt.todo.dto.request.TodoRequest;
import org.jh.oauthjwt.todo.dto.response.TodoResponse;
import org.jh.oauthjwt.todo.service.TodoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TodoController {

    private final TodoService todoService;

    public TodoController(final TodoService todoService) {
        this.todoService = todoService;
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
}