package be.ucll.unit.scheduler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import be.ucll.model.Item;
import be.ucll.model.User;
import be.ucll.model.UserDeviceToken;
import be.ucll.model.UserItem;
import be.ucll.repository.UserItemRepository;
import be.ucll.scheduler.ExpiryScheduler;
import be.ucll.service.PushNotificationService;

@ExtendWith(MockitoExtension.class)
public class ExpirySchedulerTest {

    @Mock
    private UserItemRepository userItemRepository;

    @Mock
    private PushNotificationService pushNotificationService;

    @InjectMocks
    private ExpiryScheduler expiryScheduler;

    // Helper method to create linked USerItem
    private UserItem createTestItem(String itemName, LocalDate expirationDate) {
        User user = new User("testUser", "test@example.com", "Pass123!");
        UserDeviceToken token = new UserDeviceToken(user, "token-123", "Iphone 13");
        user.addDeviceToken(token);

        Item item = new Item(itemName, Item.Type.FRUIT);

        UserItem userItem = new UserItem(user, item, expirationDate);
        return userItem;
    }

    @Test
    void checkExpiries_expiresToday() {
        // Given
        UserItem item = createTestItem("Banana", LocalDate.now());
        when(userItemRepository.findPotentialExpiries(any())).thenReturn(List.of(item));

        // When
        expiryScheduler.checkExpiries();

        // Then
        verify(pushNotificationService).sendToDevice(eq("token-123"), eq("Your Banana expires today!"));
        assertThat(item.getLastNotifiedAt()).isNotNull();
    }

    @Test
    void checkExpiries_reminderDay() {
        // Given
        UserItem item = createTestItem("Milk", LocalDate.now().plusDays(3));
        when(userItemRepository.findPotentialExpiries(any())).thenReturn(List.of(item));

        // When
        expiryScheduler.checkExpiries();

        // Then
        verify(pushNotificationService).sendToDevice(eq("token-123"), eq("Reminder: your Milk expires in 3 days!"));
    }

    @Test
    void checkExpiries_skipNonReminderDay() {
        // Given
        UserItem item = createTestItem("Cheese", LocalDate.now().plusDays(5));
        when(userItemRepository.findPotentialExpiries(any())).thenReturn(List.of(item));

        // When
        expiryScheduler.checkExpiries();

        // Then
        verify(pushNotificationService, never()).sendToDevice(any(), any());
        assertThat(item.getLastNotifiedAt()).isNull();
    }

    @Test
    void checkExpiries_skipIfNotifiedAlready() {
        // Given
        UserItem item = createTestItem("Yoghurt", LocalDate.now());
        item.setLastNotifiedAt(LocalDateTime.now().minusHours(1));

        when(userItemRepository.findPotentialExpiries(any())).thenReturn(List.of(item));

        // When
        expiryScheduler.checkExpiries();

        // Then
        verify(pushNotificationService, never()).sendToDevice(any(), any());
    }

    @Test
    void checkExpiries_openedLogic_overrides() {
        // Given
        UserItem item = createTestItem("Orange Juice", LocalDate.now().plusDays(10));

        item.setOpenedDate(LocalDate.now());
        item.setOpenedRule(3);

        when(userItemRepository.findPotentialExpiries(any())).thenReturn(List.of(item));

        // When
        expiryScheduler.checkExpiries();

        // Then
        verify(pushNotificationService).sendToDevice(eq("token-123"),
                eq("Reminder: your Orange Juice expires in 3 days!"));
    }

    @Test
    void checkExpiries_alreadyExpired() {
        // Given
        UserItem item = createTestItem("Bread", LocalDate.now().minusDays(1));
        when(userItemRepository.findPotentialExpiries(any())).thenReturn(List.of(item));

        // When
        expiryScheduler.checkExpiries();

        // Then
        verify(pushNotificationService).sendToDevice(eq("token-123"), eq("Your Bread has expired!"));
    }

    @Test
    void checkExpiries_noTokens() {
        // Given
        UserItem item = createTestItem("Banana", LocalDate.now());
        item.getUser().getDeviceTokens().clear();

        when(userItemRepository.findPotentialExpiries(any())).thenReturn(List.of(item));

        // When
        expiryScheduler.checkExpiries();

        // Then
        verify(pushNotificationService, never()).sendToDevice(any(), any());
        // getLastNotified should not be null -> would cause infinite loops
        assertThat(item.getLastNotifiedAt()).isNotNull();
    }
}
