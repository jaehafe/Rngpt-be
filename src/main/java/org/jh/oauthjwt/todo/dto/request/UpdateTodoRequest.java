package org.jh.oauthjwt.todo.dto.request;

import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jh.oauthjwt.todo.domain.type.PriorityType;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTodoRequest {

    @Size(max = 20, message = "할 일의 이름은 20자를 초과할 수 없습니다.")
    private String title;

    @Size(max = 100, message = "할 일의 내용은 100자를 초과할 수 없습니다.")
    private String body;

    private LocalDateTime dueDate;

    private PriorityType priority;

    private Boolean isCompleted;
}
