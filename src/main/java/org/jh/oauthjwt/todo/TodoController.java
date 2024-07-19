package org.jh.oauthjwt.todo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TodoController {

    @GetMapping("/api/todo")
    public ResponseEntity<String> getTodos() {
        // response: json 형식으로 반환
        return ResponseEntity.ok("Todo list");
    }
}
