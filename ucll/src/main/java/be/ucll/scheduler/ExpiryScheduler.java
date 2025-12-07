package be.ucll.scheduler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import be.ucll.model.UserItem;
import be.ucll.repository.UserItemRepository;
import be.ucll.service.PushNotificationService;

@Component
public class ExpiryScheduler {
  
  private static final Logger logger = LoggerFactory.getLogger(ExpiryScheduler.class);

  private final UserItemRepository userItemRepository;
  private final PushNotificationService pushNotificationService;

  private static final List<Long> REMINDER_DAYS = List.of(7L, 3L, 1L);

  public ExpiryScheduler(UserItemRepository userItemRepository, PushNotificationService pushNotificationService) {
    this.userItemRepository = userItemRepository;
    this.pushNotificationService = pushNotificationService;
  }

  @Scheduled(cron = "0 0 0 * * *")
  public void checkExpiries() {
    logger.info("Running expiry scheduler...");

    LocalDate today = LocalDate.now();

    for (UserItem userItem : userItemRepository.findAll()) {
      LocalDate expiryDate = calculateExpiryDate(userItem);

      long daysUntilExpiry = ChronoUnit.DAYS.between(today, expiryDate);

      if (REMINDER_DAYS.contains(daysUntilExpiry) && shouldSendReminder(userItem)) {
          sendNotification(userItem, "Reminder: your item " + userItem.getItem().getName() + " expires in " + daysUntilExpiry + " days!");
          userItem.setLastNotifiedAt(LocalDateTime.now());
          userItemRepository.save(userItem);
      }

      if (!expiryDate.isAfter(today) && shouldSendReminder(userItem)) {
        sendNotification(userItem, "Your item " + userItem.getItem().getName() + " has expired!");
        userItem.setLastNotifiedAt(LocalDateTime.now());
        userItemRepository.save(userItem);
      }
    }
  }

  private LocalDate calculateExpiryDate(UserItem userItem) {
    if (userItem.getOpenedDate() != null) {
      LocalDate openedExpiry = userItem.getOpenedDate().plusDays(userItem.getOpenedRule());
      return openedExpiry.isBefore(userItem.getExpirationDate()) ? openedExpiry : userItem.getExpirationDate();
    } else {
      return userItem.getExpirationDate();
    }
  }

  private boolean shouldSendReminder(UserItem userItem) {
    LocalDateTime lastNotified = userItem.getLastNotifiedAt();
      if (lastNotified == null) return true;
      return lastNotified.toLocalDate().isBefore(LocalDate.now());
    }

  private void sendNotification(UserItem userItem, String message) {
    userItem.getUser().getDeviceTokens().forEach(token ->
        pushNotificationService.sendToDevice(token.getToken(), "ResQFood Notification", message)
    );
  }
}
