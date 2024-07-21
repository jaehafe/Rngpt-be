package org.jh.oauthjwt.todo.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TodoCompletionRequest {

    @JsonProperty("isCompleted")
    private boolean isCompleted;
}
