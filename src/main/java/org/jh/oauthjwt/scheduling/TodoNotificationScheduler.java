package org.jh.oauthjwt.scheduling;

import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.jh.oauthjwt.scheduling.service.NotificationService;
import org.jh.oauthjwt.todo.domain.Todo;
import org.jh.oauthjwt.todo.domain.repository.TodoRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TodoNotificationScheduler {

    private final TodoRepository todoRepository;
    private final NotificationService notificationService;

    public TodoNotificationScheduler(TodoRepository todoRepository, NotificationService notificationService) {
        this.todoRepository = todoRepository;
        this.notificationService = notificationService;
    }

//    @Scheduled(fixedRate = 60000) // 1분마다 실행
    @Scheduled(fixedRate = 50000000) // 5초마다 실행
    public void checkAndNotify() {
        log.info("Starting scheduled task: checkAndNotify at {}", LocalDateTime.now());

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tenMinutesLater = now.plusMinutes(10);

        List<Todo> todosToNotify = todoRepository.findByDueDateBetweenAndNotifiedFalse(now, tenMinutesLater);

        for (Todo todo : todosToNotify) {
            notificationService.sendNotification(todo);
            todo.setNotified(true);
            todoRepository.save(todo);
        }
        log.info("Finished scheduled task: checkAndNotify, processed {} todos", todosToNotify.size());
    }
}
