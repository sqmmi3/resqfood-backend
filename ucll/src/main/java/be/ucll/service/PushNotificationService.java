package be.ucll.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;

@Service
public class PushNotificationService {

  private static final Logger logger = LoggerFactory.getLogger(PushNotificationService.class);
  private final FirebaseMessaging firebaseMessaging;

  public PushNotificationService(FirebaseMessaging firebaseMessaging) {
    this.firebaseMessaging = firebaseMessaging;
  }

  public void sendToDevice(String token, String title, String body) {
    try {
        Message message = Message.builder()
          .setToken(token)
          .putData("title", title)
          .putData("body", body)
          .build();

        firebaseMessaging.send(message);

        logger.info("Successfully sent FCM push notification to token {}", token);

    } catch (Exception e) {
        logger.error("Failed to send FCM push notification: ", e);
    }
  }
}
