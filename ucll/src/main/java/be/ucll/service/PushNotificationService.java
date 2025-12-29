package be.ucll.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

@Service
public class PushNotificationService {

  private static final Logger logger = LoggerFactory.getLogger(PushNotificationService.class);
  private final FirebaseMessaging firebaseMessaging;

  public PushNotificationService(@Nullable FirebaseMessaging firebaseMessaging) {
    this.firebaseMessaging = firebaseMessaging;
  }

  public void sendToDevice(String token, String notificationMessage) {
    if (this.firebaseMessaging == null) {
      logger.warn("Skipping FCM push notification (Firebase not initialized): {}", notificationMessage);
      return;
    }

    try {
      Notification notification = Notification.builder()
        .setTitle("ResQFood")
        .setBody(notificationMessage)
        .build();

      Message message = Message.builder()
        .setToken(token)
        .setNotification(notification)
        .setAndroidConfig(AndroidConfig.builder()
            .setPriority(AndroidConfig.Priority.HIGH)
            .setNotification(AndroidNotification.builder()
                .setIcon("ic_notification")
                .setColor("#4caf50")
                .setPriority(AndroidNotification.Priority.HIGH)
                .build())
            .build())
        .putData("title", "ResQFood")
        .putData("body", notificationMessage)
        .build();

      firebaseMessaging.send(message);
      logger.info("Successfully sent FCM push notification to token {}", token);
    } catch (Exception e) {
        logger.error("Failed to send FCM push notification: ", e);
    }
  }
}
