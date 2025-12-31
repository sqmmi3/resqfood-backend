package be.ucll.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import be.ucll.exception.DomainException;
import be.ucll.model.Notification;
import be.ucll.model.User;
import be.ucll.model.UserDeviceToken;
import be.ucll.repository.NotificationRepository;
import be.ucll.service.NotificationService;
import be.ucll.service.PushNotificationService;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {
    
    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private PushNotificationService pushNotificationService;

    @InjectMocks
    private NotificationService notificationService;

    @Mock
    private User mockUser;

    @Test
    void createAndSendNotification_ShouldSaveNotificationAndCallPushService() {
        String title = "Food Expiring!";
        String message = "Your milk is about to about to go bad.";
        Long relatedId = 101L;

        UserDeviceToken phone = new UserDeviceToken(mockUser, "token-phone-123", "iPhone 13");
        UserDeviceToken tablet = new UserDeviceToken(mockUser, "token-tablet-456", "iPad Air");

        when(mockUser.getDeviceTokens()).thenReturn(List.of(phone, tablet));

        notificationService.createAndSendNotification(mockUser, title, message, relatedId);

        verify(notificationRepository, times(1)).save(any(Notification.class));

        verify(pushNotificationService).sendToDevice("token-phone-123", message);
        verify(pushNotificationService).sendToDevice("token-tablet-456", message);
    }

    @Test
    void markAsRead_ShouldUpdateStatus_WhenUserIsAuthorized() {
        Long notificationId = 1L;
        String username = "JohnDoe";
        Notification notification = new Notification(mockUser, "Title", "Msg", null);

        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));
        when(mockUser.getUsername()).thenReturn(username);

        notificationService.markAsRead(notificationId, username);

        assertThat(notification.getIsRead()).isTrue();
        verify(notificationRepository).save(notification);
    }

    @Test
    void markAsRead_ShouldThrowException_WhenUserIsUnauthorized() {
        Long notificationId = 1L;
        String ownerName = "OwnerUser";
        String intruderName = "IntruderUser";

        Notification notification = new Notification(mockUser, "Title", "Msg", null);
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));
        when(mockUser.getUsername()).thenReturn(ownerName);

        assertThatThrownBy(() -> notificationService.markAsRead(notificationId, intruderName))
            .isInstanceOf(DomainException.class)
            .hasMessage("Unauthorized");
        
        verify(notificationRepository, never()).save(any());
    }

    @Test
    void markAsRead_ShouldThrowException_whenNotificationNotFound() {
        Long nonExistentId = 2L;
        String username = "JohnDoe";

        when(notificationRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificationService.markAsRead(nonExistentId, username))
            .isInstanceOf(DomainException.class)
            .hasMessage("Notification not found!");
        
        verify(notificationRepository, never()).save(any());
    }

    @Test
    void deleteNotification_ShouldCallDelete_WhenAuthorized() {
        Long id = 5L;
        String username = "tester";
        Notification notification = new Notification(mockUser, "Title", "Msg", null);

        when(notificationRepository.findById(id)).thenReturn(Optional.of(notification));
        when(mockUser.getUsername()).thenReturn(username);

        notificationService.deleteNotification(id, username);

        verify(notificationRepository).delete(notification);
    }

    @Test
    void getUnreadCount_ShouldReturnCountFromRepo() {
        String username = "user1";
        when(notificationRepository.countByUser_UsernameAndIsReadFalse(username)).thenReturn(5L);

        long count = notificationService.getUnreadCount(username);

        assertThat(count).isEqualTo(5L);
    }
}
