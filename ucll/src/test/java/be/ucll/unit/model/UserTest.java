package be.ucll.unit.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import be.ucll.model.Household;
import be.ucll.model.Item;
import be.ucll.model.User;
import be.ucll.model.UserDeviceToken;
import be.ucll.model.UserItem;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.time.LocalDateTime;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class UserTest {

    // Global given
    private final String validUsername = "John_Doe123";
    private final String validEmail = "john@doe.com";
    private final String validPassword = "Password1!";

    private Validator validator;

    @Mock
    private UserItem mockUserItem;

    @Mock
    private Item mockItem;

    @Mock
    private Household household;

    @Mock
    private UserDeviceToken mockDeviceToken;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // Happy test paths
    @Test
    void createUser_happyPath() {
        // When
        User user = new User(validUsername, validEmail, validPassword);

        // Then
        assertThat(user.getUsername()).isEqualTo(validUsername);
        assertThat(user.getEmail()).isEqualTo(validEmail);
        assertThat(user.getPassword()).isEqualTo(validPassword);
        assertThat(user.getUserItems()).isEmpty();
        assertThat(user.getDeviceTokens()).isEmpty();
    }

    @Test
    void updateUser_happyPath() {
        // Given
        User user = new User(validUsername, validEmail, validPassword);
        String newUsername = "Jane_Doe456";
        String newEmail = "jane@doe.com";
        String newPassword = "NewPassword2@";

        // When
        user.setUsername(newUsername);
        user.setEmail(newEmail);
        user.setPassword(newPassword);

        // Then
        assertThat(user.getUsername()).isEqualTo(newUsername);
        assertThat(user.getEmail()).isEqualTo(newEmail);
        assertThat(user.getPassword()).isEqualTo(newPassword);
    }

    @Test
    void addUserItem_happyPath() {
        // Given
        User user = new User(validUsername, validEmail, validPassword);

        // When
        user.addUserItem(mockUserItem);

        // Then
        assertThat(user.getUserItems()).containsExactly(mockUserItem);
        verify(mockUserItem).setUser(user);
    }

    @Test
    void removeUserItem_happyPath() {
        // Given
        User user = new User(validUsername, validEmail, validPassword);
        user.addUserItem(mockUserItem);

        // When
        user.removeUserItem(mockUserItem);

        // Then
        assertThat(user.getUserItems()).isEmpty();
        verify(mockUserItem).setUser(null);
    }

    @Test
    void getItems_logicTest() {
        // Given
        User user = new User(validUsername, validEmail, validPassword);
        user.addUserItem(mockUserItem);

        // Mock behavior -> when getItem is called on mockUserItem, return mockItem
        when(mockUserItem.getItem()).thenReturn(mockItem);

        // When
        var result = user.getItems();

        // Then
        assertThat(result).containsExactly(mockItem);
    }

    @Test
    void addDeviceToken_happyPath() {
        // Given
        User user = new User(validUsername, validEmail, validPassword);

        // When
        user.addDeviceToken(mockDeviceToken);

        // Then
        assertThat(user.getDeviceTokens()).containsExactly(mockDeviceToken);
        verify(mockDeviceToken).setUser(user);
    }

    @Test
    void removeDeviceToken_happyPath() {
        // Given
        User user = new User(validUsername, validEmail, validPassword);
        user.addDeviceToken(mockDeviceToken);

        // When
        user.removeDeviceToken(mockDeviceToken);

        // Then
        assertThat(user.getDeviceTokens()).doesNotContain(mockDeviceToken);
        verify(mockDeviceToken).setUser(null);
    }

    // Unhappy test paths

    @Test
    void validateUser_invalidUsername_tooShort() {
        // Given
        User user = new User("ab", validEmail, validPassword);

        // When
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        // Then
        assertThat(violations).anyMatch(v -> v.getMessage().contains("Username must be between 3 and 50 characters."));
    }

    @Test
    void validateUser_invalidUsername_pattern() {
        // Given
        User user = new User("Invalid Name!", validEmail, validPassword);

        // When
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        // Then
        assertThat(violations).anyMatch(
                v -> v.getMessage().contains("Username can only contain letters, numbers, and underscores"));
    }

    @Test
    void validateUser_invalidEmail() {
        // Given
        User user = new User(validUsername, "invalid-email", validPassword);

        // When
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        // Then
        assertThat(violations).anyMatch(v -> v.getMessage().contains("Email should be valid."));
    }

    @ParameterizedTest
    @ValueSource(strings = {"password123!", "PASSWORD123!", "Password!", "Password1"})
    void validateUser_password_invalid(String invalidPassword) {
        // Given
        User user = new User(validUsername, validEmail, invalidPassword);

        // When
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        // Then
        assertThat(violations).anyMatch(v -> v.getMessage().contains(
                "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character."));
    }

    @Test
    void validateUser_password_tooShort() {
        // Given
        User user = new User(validUsername, validEmail, "P1!");

        // When
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        // Then
        assertThat(violations).isNotEmpty();
    }

    @Test
    void incrementRescued_shouldIncreaseCount() {
        User user = new User(validUsername, validEmail, validPassword);
        assertThat(user.getItemsRescued()).isZero();

        user.incrementRescued();
        user.incrementRescued();

        assertThat(user.getItemsRescued()).isEqualTo(2);
    }

    @Test
    void onCreate_shouldSetCreateAt() {
        User user = new User(validUsername, validEmail, validPassword);
        assertThat(user.getCreatedAt()).isNull();

        user.onCreate();

        assertThat(user.getCreatedAt()).isNotNull();
        assertThat(user.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    void toString_shouldHandleUserWithNoItems() {
        User user = new User(validUsername, validEmail, validPassword);

        String result = user.toString();

        assertThat(result)
                .contains("username=" + validUsername)
                .contains("items=[]")
                .contains("household=none");
    }

    @Test
    void userJoinsHousehold_ShouldReturnHouseholdEntity() {
        User user = new User(validUsername, validEmail, validPassword);
        assertThat(user.getHousehold()).isNull();

        user.setHousehold(household);

        assertThat(user.getHousehold()).isEqualTo(household);
    }
}
