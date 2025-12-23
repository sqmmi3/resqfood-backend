package be.ucll.scheduler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import be.ucll.model.UserDeviceToken;
import be.ucll.model.UserItem;
import be.ucll.repository.UserItemRepository;
import be.ucll.service.PushNotificationService;
import jakarta.transaction.Transactional;

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

  @Scheduled(cron = "0 0 9 * * *")
  @Transactional
  public void checkExpiries() {
    logger.info("Running expiry scheduler...");

    LocalDate today = LocalDate.now();
    LocalDate sevenDaysFromNow = today.plusDays(7);

    List<UserItem> candidates = userItemRepository.findPotentialExpiries(sevenDaysFromNow);

    for (UserItem userItem : candidates) {
      if (!shouldSendReminder(userItem)) continue;

      LocalDate expiryDate = calculateExpiryDate(userItem);
      long daysUntilExpiry = ChronoUnit.DAYS.between(today, expiryDate);

      String message = determineMessage(userItem.getItem().getName(), daysUntilExpiry);

      if (message != null) {
        sendNotification(userItem, message);
        userItem.setLastNotifiedAt(LocalDateTime.now());
      }
    }
  }

  private String determineMessage(String itemName, long daysUntil) {
    if (daysUntil == 0) return "Your " + itemName + " expires today!";
    if (daysUntil < 0) return "Your " + itemName + " has expired!";
    if (REMINDER_DAYS.contains(daysUntil)) {
      return "Reminder: your " + itemName + " expires in " + daysUntil + " days!";
    }
    return null;
  }

  private LocalDate calculateExpiryDate(UserItem userItem) {
    if (userItem.getOpenedDate() != null) {
      LocalDate openedExpiry = userItem.getOpenedDate().plusDays(userItem.getOpenedRule());
      return openedExpiry.isBefore(userItem.getExpirationDate()) ? openedExpiry : userItem.getExpirationDate();
    }
    return userItem.getExpirationDate();
  }

  private boolean shouldSendReminder(UserItem userItem) {
    if (userItem.getLastNotifiedAt() == null) return true;
    return userItem.getLastNotifiedAt().toLocalDate().isBefore(LocalDate.now());
  }

  private void sendNotification(UserItem userItem, String message) {
    List<UserDeviceToken> tokens = userItem.getUser().getDeviceTokens();
    if (tokens.isEmpty()) return;

    tokens.forEach(token ->
        pushNotificationService.sendToDevice(token.getToken(), message)
    );
  }
}
