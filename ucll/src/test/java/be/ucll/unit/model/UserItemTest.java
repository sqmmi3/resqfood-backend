package be.ucll.unit.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import be.ucll.model.Item;
import be.ucll.model.User;
import be.ucll.model.UserItem;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@ExtendWith(MockitoExtension.class)
class UserItemTest {

    // Global given

    private final LocalDate futureDate = LocalDate.now().plusDays(10);
    private final LocalDate pastDate = LocalDate.now().minusDays(5);
    private final LocalDate today = LocalDate.now();
    private final String validDescription = "Leftover pasta";

    private Validator validator;

    @Mock
    private User mockUser;

    @Mock
    private Item mockItem;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void constructor_minimal_happyPath() {

        // When
        UserItem userItem = new UserItem(mockUser, mockItem, futureDate);

        // Then
        assertThat(userItem.getUser()).isEqualTo(mockUser);
        assertThat(userItem.getItem()).isEqualTo(mockItem);
        assertThat(userItem.getExpirationDate()).isEqualTo(futureDate);
        assertThat(userItem.getOpenedDate()).isNull();
        assertThat(userItem.getDescription()).isNull();
    }

    @Test
    void constructor_full_happyPath() {

        // When
        UserItem userItem = new UserItem(mockUser, mockItem, futureDate, pastDate, validDescription);

        // Then
        assertThat(userItem.getUser()).isEqualTo(mockUser);
        assertThat(userItem.getItem()).isEqualTo(mockItem);
        assertThat(userItem.getExpirationDate()).isEqualTo(futureDate);
        assertThat(userItem.getOpenedDate()).isEqualTo(pastDate);
        assertThat(userItem.getDescription()).isEqualTo(validDescription);
    }

    @Test
    void getOpenedRule_fallbackToItem() {
        // Given
        UserItem userItem = new UserItem(mockUser, mockItem, futureDate);
        userItem.setOpenedRule(null);

        when(mockItem.getOpenedRule()).thenReturn(7);

        // When
        Integer result = userItem.getOpenedRule();

        // Then
        assertThat(result).isEqualTo(7);
    }

    @Test
    void getOpenedRule_useSpecificRule() {
        // Given
        UserItem userItem = new UserItem(mockUser, mockItem, futureDate);
        userItem.setOpenedRule(2);

        // When
        Integer result = userItem.getOpenedRule();

        // Then
        assertThat(result).isEqualTo(2);
    }

    @Test
    void setOpenedDate_logic_happyPath() {
        // Given
        UserItem userItem = new UserItem(mockUser, mockItem, futureDate);
        LocalDate validOpened = LocalDate.now();

        // When
        userItem.setOpenedDate(validOpened);

        // Then
        assertThat(userItem.getOpenedDate()).isEqualTo(validOpened);
    }

    @Test
    void setOpenedDate_logic_unhappyPath() {
        // Given
        UserItem userItem = new UserItem(mockUser, mockItem, today);
        LocalDate invalidOpened = today.plusDays(1);

        // When / Then
        assertThatThrownBy(() -> userItem.setOpenedDate(invalidOpened)).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Opened date cannot be after expiration date.");

    }

    @Test
    void validate_expirationDate_past() {
        // Given
        UserItem userItem = new UserItem(mockUser, mockItem, pastDate);

        // When
        Set<ConstraintViolation<UserItem>> violations = validator.validate(userItem);

        // Then
        assertThat(violations)
                .anyMatch(v -> v.getMessage().contains("Expiration date must be in the future or present."));
    }

    @Test
    void validate_openedDate_future() {
        // Given
        LocalDate farFuture = LocalDate.now().plusYears(1);
        LocalDate nearFuture = LocalDate.now().plusDays(1);

        UserItem userItem = new UserItem(mockUser, mockItem, farFuture, nearFuture);

        // When
        Set<ConstraintViolation<UserItem>> violations = validator.validate(userItem);

        // Then
        assertThat(violations).anyMatch(v -> v.getMessage().contains("Opened date must be in the past or present."));
    }

    @Test
    void validate_openedRule_negative() {
        // Given
        UserItem userItem = new UserItem(mockUser, mockItem, futureDate, pastDate, -3);

        // When
        Set<ConstraintViolation<UserItem>> violations = validator.validate(userItem);

        // Then
        assertThat(violations).anyMatch(v -> v.getMessage().contains("Opened rule must be at least 1."));
    }

    @Test
    void validate_description_tooLong() {
        // Given
        String longDescription = "A".repeat(129);
        UserItem userItem = new UserItem(mockUser, mockItem, futureDate, longDescription);

        // When
        Set<ConstraintViolation<UserItem>> violations = validator.validate(userItem);

        // Then
        assertThat(violations)
                .anyMatch(v -> v.getMessage().contains("Description cannot be longer than 128 characters."));
    }

}