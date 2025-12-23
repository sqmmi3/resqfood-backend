package be.ucll.unit.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import be.ucll.model.User;
import be.ucll.model.UserDeviceToken;

@ExtendWith(MockitoExtension.class)
public class UserDeviceTokenTest {

    // Global given
    private final String validToken = "fcm-token-12345";
    private final String validDeviceName = "Pixel 7 Pro";

    @Mock
    private User mockUser;

    @Test
    void createToken_happyPath() {
        // When
        UserDeviceToken userDeviceToken = new UserDeviceToken(mockUser, validToken, validDeviceName);

        // Then
        assertThat(userDeviceToken.getUser()).isEqualTo(mockUser);
        assertThat(userDeviceToken.getToken()).isEqualTo(validToken);
        assertThat(userDeviceToken.getDeviceName()).isEqualTo(validDeviceName);
        assertThat(userDeviceToken.getId()).isNull(); // ID is generaeted by the database -> null here
    }

    @Test
    void updateToken_happyPath() {
        // Given
        UserDeviceToken userDeviceToken = new UserDeviceToken(mockUser, validToken, validDeviceName);
        String newToken = "new-token-67890";
        String newDevice = "iPhone 14 Pro";

        // Mock different user for update
        User newUser = new User("NewUser", "new@test.com", "Pass123!");

        // When
        userDeviceToken.setToken(newToken);
        userDeviceToken.setDeviceName(newDevice);
        userDeviceToken.setUser(newUser);
        userDeviceToken.setId(5L);

        // Then
        assertThat(userDeviceToken.getToken()).isEqualTo(newToken);
        assertThat(userDeviceToken.getDeviceName()).isEqualTo(newDevice);
        assertThat(userDeviceToken.getUser()).isEqualTo(newUser);
        assertThat(userDeviceToken.getId()).isEqualTo(5L);
    }

    @Test
    void toString_happyPath() {
        // Given
        // Mock user to return a username
        when(mockUser.getUsername()).thenReturn("TestUser");

        UserDeviceToken userDeviceToken = new UserDeviceToken(mockUser, validToken, validDeviceName);
        userDeviceToken.setId(1L);

        // When
        String result = userDeviceToken.toString();

        // Then
        assertThat(result)
                .isEqualTo("UserDeviceToken{id=1, user=TestUser, token=fcm-token-12345, deviceName=Pixel 7 Pro}");
    }

    @Test
    void toString_unhappyPath_nullUser() {
        // Given
        UserDeviceToken userDeviceToken = new UserDeviceToken(null, validToken, validDeviceName);
        userDeviceToken.setId(1L);

        // When / Then

        assertThatThrownBy(userDeviceToken::toString).isInstanceOf(NullPointerException.class);
    }

}
