package be.ucll.unit.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;

import be.ucll.service.PushNotificationService;

@ExtendWith(MockitoExtension.class)
public class PushNotificationServiceTest {

    // Global given
    private final String validToken = "device-token-123";
    private final String validMessage = "Your food is expiring soon!";

    @Mock
    private FirebaseMessaging firebaseMessaging;

    @InjectMocks
    private PushNotificationService pushNotificationService;

    @Test
    void sendToDevice_happyPath() throws FirebaseMessagingException {
        // Given
        // Mock the send method to return dummy ID
        when(firebaseMessaging.send(any(Message.class))).thenReturn("projects/resqfood/messages/msg_id");

        // When
        pushNotificationService.sendToDevice(validToken, validMessage);

        // Then
        // Verify that send was called exactly once
        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(firebaseMessaging, times(1)).send(messageCaptor.capture());
    }

    @Test
    void sendToDevice_firebaseError_unhappyPath() throws FirebaseMessagingException {
        // Given
        // Simulate firebase failing
        when(firebaseMessaging.send(any(Message.class))).thenThrow(new RuntimeException("Firebase unavailable"));

        // When / Then
        // Check that the exception wasn't thrown by the service
        assertThatCode(() -> pushNotificationService.sendToDevice(validToken, validMessage)).doesNotThrowAnyException();

        // Check if we still attempted to send
        verify(firebaseMessaging, times(1)).send(any(Message.class));
    }

    @Test
    void sendToDevice_nullToken_unhappyPath() {

        // When / Then
        assertThatCode(() -> pushNotificationService.sendToDevice(null, validMessage)).doesNotThrowAnyException();

        // Check that we never called send
        try {
            verify(firebaseMessaging, never()).send(any(Message.class));
        } catch (FirebaseMessagingException e) {
            // Should not happen
        }

    }

}
