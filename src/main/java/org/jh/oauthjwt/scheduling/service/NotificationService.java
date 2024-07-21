package org.jh.oauthjwt.scheduling.service;

import lombok.extern.slf4j.Slf4j;
import org.jh.oauthjwt.todo.domain.Todo;
import org.jh.oauthjwt.todo.service.TodoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(TodoService.class);

    public void sendNotification(Todo todo) {
        log.info("Sending notification for todo: {}", todo.getTitle());
    }
}
