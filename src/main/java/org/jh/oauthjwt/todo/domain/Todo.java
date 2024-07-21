package org.jh.oauthjwt.todo.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jh.oauthjwt.global.BaseEntity;
import org.jh.oauthjwt.todo.dto.request.TodoRequest;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Todo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String body;

    @Column(nullable = false)
    private boolean isCompleted = false;

    @Column
    private LocalDateTime dueDate;

    private Todo(String title, String body, LocalDateTime dueDate) {
        this.title = title;
        this.body = body;
        this.isCompleted = false;
        this.dueDate = dueDate;
    }

    public static Todo of(final TodoRequest todoRequest) {
        return new Todo(
                todoRequest.getTitle(),
                todoRequest.getBody(),
                todoRequest.getDueDate()
        );
    }

    public void update(final TodoRequest todoRequest) {
        this.title = todoRequest.getTitle();
        this.body = todoRequest.getBody();
        this.dueDate = todoRequest.getDueDate();
    }

//    @Enumerated(EnumType.STRING)
//    private Priority priority;
//
//    @Enumerated(EnumType.STRING)
//    private Category category;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id")
//    private User assignedTo;
//
//    @ElementCollection
//    @CollectionTable(name = "todo_tags", joinColumns = @JoinColumn(name = "todo_id"))
//    @Column(name = "tag")
//    private Set<String> tags = new HashSet<>();
}
