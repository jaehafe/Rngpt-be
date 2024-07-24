package org.jh.oauthjwt.todo.domain.repository;


import java.time.LocalDateTime;
import java.util.List;

import org.jh.oauthjwt.todo.domain.Todo;
import org.jh.oauthjwt.todo.domain.type.CategoryType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findByDueDateBetweenAndNotifiedFalse(LocalDateTime start, LocalDateTime end);

    List<Todo> findByCategory(CategoryType category);

    Page<Todo> findByCreatorEmail(String email, Pageable pageable);

}
