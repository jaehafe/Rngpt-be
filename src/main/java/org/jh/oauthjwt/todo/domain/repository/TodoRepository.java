package org.jh.oauthjwt.todo.domain.repository;


import org.jh.oauthjwt.todo.domain.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends JpaRepository<Todo, Long> {

}
