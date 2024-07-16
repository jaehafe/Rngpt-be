package org.jh.oauthjwt.fcm;

import com.google.firebase.messaging.FirebaseMessagingException;
import org.jh.oauthjwt.fcm.dto.NotificationRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NotificationController {

    private final FCMService fcmService;

    public NotificationController(FCMService fcmService) {
        this.fcmService = fcmService;
    }

    @PostMapping("/send-notification")
    public ResponseEntity<String> sendNotification(@RequestBody NotificationRequestDTO request) throws FirebaseMessagingException {
        String result = fcmService.sendNotification(request);
        return ResponseEntity.ok(result);
    }
}
