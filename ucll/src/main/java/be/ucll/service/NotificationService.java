package be.ucll.service;

import java.util.List;

import org.springframework.stereotype.Service;

import be.ucll.exception.DomainException;
import be.ucll.model.Notification;
import be.ucll.model.User;
import be.ucll.repository.NotificationRepository;
import jakarta.transaction.Transactional;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final PushNotificationService pushNotificationService;

    public NotificationService(NotificationRepository notificationRepository,
            PushNotificationService pushNotificationService) {
        this.notificationRepository = notificationRepository;
        this.pushNotificationService = pushNotificationService;
    }

    // This method replaces direct call to pushNotificationService
    @Transactional
    public void createAndSendNotification(User user, String title, String message, Long relatedItemId) {
        // Save to db (History)
        Notification notification = new Notification(user, title, message, relatedItemId);
        notificationRepository.save(notification);

        // Send to firebase (Real time notif)
        user.getDeviceTokens().forEach(token -> pushNotificationService.sendToDevice(token.getToken(), message));
    }

    public List<Notification> getUserNotifications(String username) {
        return notificationRepository.findByUser_UsernameOrderByTimestampDesc(username);
    }

    public void markAsRead(Long notificationId, String username) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new DomainException("Notification not found!"));

        if (!notification.getUser().getUsername().equals(username)) {
            throw new DomainException("Unauthorized");
        }

        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    @Transactional
    public void deleteNotification(Long id, String username) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new DomainException("Notification not found with id: " + id));

        if (!notification.getUser().getUsername().equals(username)) {
            throw new DomainException("You do not have permission to delete this notification");
        }

        notificationRepository.delete(notification);
    }
}
