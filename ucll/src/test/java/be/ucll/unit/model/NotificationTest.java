package be.ucll.unit.model;

import be.ucll.model.Notification;
import be.ucll.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class NotificationTest {

    @Mock
    private User mockUser;

    @Test
    void constructor_ShouldInitializeCorrectly() {
        String title = "Food Expiring!";
        String message = "Your Milk is about to go bad.";
        Long relatedId = 101L;

        Notification notification = new Notification(mockUser, title, message, relatedId);

        assertThat(notification.getUser()).isEqualTo(mockUser);
        assertThat(notification.getTitle()).isEqualTo(title);
        assertThat(notification.getMessage()).isEqualTo(message);
        assertThat(notification.getRelatedItemId()).isEqualTo(relatedId);
        
        assertThat(notification.getIsRead()).isFalse();
        assertThat(notification.getTimestamp()).isNotNull();
        assertThat(notification.getTimestamp()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    void setters_ShouldUpdateValues() {
        // Given
        Notification notification = new Notification();
        String newTitle = "New Title";
        String newMessage = "New Message";
        LocalDateTime manualTime = LocalDateTime.now().minusDays(1);

        notification.setTitle(newTitle);
        notification.setMessage(newMessage);
        notification.setIsRead(true);
        notification.setTimestamp(manualTime);

        assertThat(notification.getTitle()).isEqualTo(newTitle);
        assertThat(notification.getMessage()).isEqualTo(newMessage);
        assertThat(notification.getIsRead()).isTrue();
        assertThat(notification.getTimestamp()).isEqualTo(manualTime);
    }

    @Test
    void noArgConstructor_ShouldInitializeWithDefaults() {
        Notification notification = new Notification();

        assertThat(notification.getIsRead()).isFalse();
        assertThat(notification.getUser()).isNull();
    }
}