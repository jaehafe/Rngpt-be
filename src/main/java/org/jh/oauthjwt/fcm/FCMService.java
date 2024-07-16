package org.jh.oauthjwt.fcm;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.jh.oauthjwt.fcm.dto.NotificationRequestDTO;
import org.springframework.stereotype.Service;

@Service
public class FCMService {

    private final FirebaseMessaging firebaseMessaging;

    public FCMService(FirebaseMessaging firebaseMessaging) {
        this.firebaseMessaging = firebaseMessaging;
    }

    public String sendNotification(NotificationRequestDTO request) throws FirebaseMessagingException {
        Notification notification = Notification
                .builder()
                .setTitle(request.getTitle())
                .setBody(request.getBody())
                .build();

        Message message = Message
                .builder()
                .setToken(request.getToken())
                .setNotification(notification)
                .build();

        try {
            String result = firebaseMessaging.send(message);
            firebaseMessaging.send(message);
            return "Notification sent" + result;
        } catch (FirebaseMessagingException e) {
            return "Error sending notification: " + e.getMessage();
        }
    }
}
