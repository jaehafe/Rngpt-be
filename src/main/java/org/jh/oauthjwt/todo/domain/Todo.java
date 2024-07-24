package org.jh.oauthjwt.todo.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jh.oauthjwt.entity.UserEntity;
import org.jh.oauthjwt.global.BaseEntity;
import org.jh.oauthjwt.todo.domain.type.CategoryType;
import org.jh.oauthjwt.todo.domain.type.PriorityType;
import org.jh.oauthjwt.todo.dto.request.CreateTodoRequest;
import org.jh.oauthjwt.todo.dto.request.UpdateTodoRequest;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Todo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;

    @Column(nullable = false)
    private boolean isCompleted = false;

    @Column(nullable = true)
    private LocalDateTime dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private PriorityType priority;

    @Column(nullable = false)
    private boolean notified = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private CategoryType category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity creator;

    private Todo(String title, String body, LocalDateTime dueDate) {
        this.title = title;
        this.body = body;
        this.isCompleted = false;
        this.dueDate = dueDate;
    }

    public static Todo of(final CreateTodoRequest todoRequest, UserEntity creator) {
        Todo todo = new Todo(
                todoRequest.getTitle(),
                todoRequest.getBody(),
                todoRequest.getDueDate()
        );
        todo.setCreator(creator);
        todo.setPriority(todoRequest.getPriority());
        todo.setCategory(todoRequest.getCategory());
        return todo;
    }

    public void update(UpdateTodoRequest request) {
        if (request.getTitle() != null) {
            this.title = request.getTitle();
        }
        if (request.getBody() != null) {
            this.body = request.getBody();
        }
        if (request.getDueDate() != null) {
            this.dueDate = request.getDueDate();
        }
        if (request.getPriority() != null) {
            this.priority = request.getPriority();
        }
        if (request.getIsCompleted() != null) {
            this.isCompleted = request.getIsCompleted();
        }
        if (request.getCategory() != null) {
            this.category = request.getCategory();
        }
    }



//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id")
//    private User assignedTo;
//
//    @ElementCollection
//    @CollectionTable(name = "todo_tags", joinColumns = @JoinColumn(name = "todo_id"))
//    @Column(name = "tag")
//    private Set<String> tags = new HashSet<>();
}
