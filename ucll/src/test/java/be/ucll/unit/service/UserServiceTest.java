package be.ucll.unit.service;

import be.ucll.exception.DomainException;
import be.ucll.model.Item;
import be.ucll.model.User;
import be.ucll.model.UserDeviceToken;
import be.ucll.model.UserItem;
import be.ucll.repository.ItemRepository;
import be.ucll.repository.UserDeviceTokenRepository;
import be.ucll.repository.UserItemRepository;
import be.ucll.repository.UserRepository;
import be.ucll.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    // Global Given
    private final Long validUserId = 1L;
    private final Long validItemId = 10L;
    private final String validUsername = "TestUser";
    private final String validEmail = "test@example.com";
    private final String rawPassword = "password123!";
    private final String encodedPassword = "encoded_password123!";

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserItemRepository userItemRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserDeviceTokenRepository userDeviceTokenRepository;

    @InjectMocks
    private UserService userService;

    // Mock user helper
    private User createMockUser() {
        return new User(validUsername, validEmail, rawPassword);
    }

    @Test
    void registerUser_happyPath() {
        // Given
        User newUser = createMockUser();

        when(userRepository.existsByUsername(validUsername)).thenReturn(false);
        when(userRepository.existsByEmail(validEmail)).thenReturn(false);
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).then(returnsFirstArg());

        // When
        User result = userService.registerUser(newUser);

        // Then
        assertThat(result.getUsername()).isEqualTo(validUsername);
        assertThat(result.getEmail()).isEqualTo(validEmail);
        assertThat(result.getPassword()).isEqualTo(encodedPassword);
        verify(userRepository).save(newUser);
    }

    @Test
    void registerUser_duplicateUsername_unhappyPath() {
        // Given
        User newUser = createMockUser();
        when(userRepository.existsByUsername(validUsername)).thenReturn(true);

        // When / Then
        assertThatThrownBy(() -> userService.registerUser(newUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Username is already registered.");
    }

    @Test
    void registerUser_duplicateEmail_unhappyPath() {
        // Given
        User newUser = createMockUser();
        when(userRepository.existsByEmail(validEmail)).thenReturn(true);

        // When / Then
        assertThatThrownBy(() -> userService.registerUser(newUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email is already registered.");
    }

    @Test
    void updateUser_happyPath() {
        // Given
        User existingUser = createMockUser();
        User updates = new User("NewName", null, null);

        when(userRepository.findById(validUserId)).thenReturn(Optional.of(existingUser));
        // ? whut
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        // When
        User result = userService.updateUser(validUserId, updates);

        // Then
        assertThat(result.getUsername()).isEqualTo("NewName");
        assertThat(result.getEmail()).isEqualTo(validEmail);
    }

    @Test
    void addItemToUser_happyPath() {
        // Given
        User user = createMockUser();
        Item item = new Item("Banana", Item.Type.FRUIT);

        LocalDate expiry = LocalDate.now().plusDays(5);

        when(userRepository.findById(validUserId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(validItemId)).thenReturn(Optional.of(item));
        when(userRepository.save(user)).thenReturn(user);

        // When
        userService.addItemToUser(validUserId, validItemId, expiry);

        // Then
        verify(userItemRepository).save(any(UserItem.class));

        verify(userRepository).save(user);
        assertThat(user.getUserItems()).hasSize(1);
    }

    @Test
    void addItemToUser_noExpiry_unhappyPath() {
        // When / Then
        assertThatThrownBy(() -> userService.addItemToUser(validUserId, validItemId, null))
                .isInstanceOf(DomainException.class)
                .hasMessage("Expiration date is required.");
    }

    @Test
    void addItemToUser_duplicate_unhappyPath() {
        // Given
        User user = createMockUser();
        Item item = spy(new Item("Banana", Item.Type.FRUIT));
        when(item.getId()).thenReturn(validItemId);

        UserItem existingLink = new UserItem(user, item, LocalDate.now());
        user.addUserItem(existingLink);

        when(userRepository.findById(validUserId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(validItemId)).thenReturn(Optional.of(item));

        // When
        userService.addItemToUser(validUserId, validItemId, LocalDate.now());

        // Then
        verify(userItemRepository, never()).save(any(UserItem.class));
    }

    @Test
    void removeItemFromUser_happyPath() {
        // Given
        User user = createMockUser();
        Item item = new Item("Banana", Item.Type.FRUIT);

        // Link item
        UserItem link = new UserItem(user, item, LocalDate.now());
        user.addUserItem(link);

        when(userRepository.findById(validUserId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(validItemId)).thenReturn(Optional.of(item));

        // When
        userService.removeItemFromUser(validUserId, validItemId);

        // Then
        verify(userItemRepository).delete(link);
        assertThat(user.getUserItems()).isEmpty();
    }

    @Test
    void removeItemFromUser_notLinked_unhappyPath() {
        // Given
        User user = createMockUser();
        Item item = new Item("Banana", Item.Type.FRUIT);

        when(userRepository.findById(validUserId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(validItemId)).thenReturn(Optional.of(item));

        // When / Then
        assertThatThrownBy(() -> userService.removeItemFromUser(validUserId, validItemId))
                .isInstanceOf(DomainException.class)
                .hasMessage("User does not have this item.");
    }

    @Test
    void addDeviceToke_happyPath() {
        // Given
        User user = createMockUser();
        when(userRepository.findByUsername(validUsername)).thenReturn(Optional.of(user));

        // When
        userService.addDeviceToken(validUsername, "token_123", "Pixel 7 Pro");

        // Then
        verify(userRepository).save(user);
        assertThat(user.getDeviceTokens()).hasSize(1);
    }

    @Test
    void addDeviceToken_skipDuplicate_happyPath() {
        // Given
        User user = createMockUser();
        UserDeviceToken existing = new UserDeviceToken(user, "token_123", "Pixel 7 Pro");
        user.addDeviceToken(existing);

        when(userRepository.findByUsername(validUsername)).thenReturn(Optional.of(user));

        // When
        userService.addDeviceToken(validUsername, "token_123", "Pixel 7 Pro");

        // Then
        assertThat(user.getDeviceTokens()).hasSize(1);
    }

    @Test
    void removeDeviceToken_happyPath() {
        // Given
        User user = createMockUser();
        UserDeviceToken existing = new UserDeviceToken(user, "token_123", "Pixel 7 Pro");
        user.addDeviceToken(existing);

        when(userDeviceTokenRepository.findByToken("token_123")).thenReturn(Optional.of(existing));

        // When
        userService.removeDeviceToken("token_123");

        // Then
        verify(userRepository).save(user);
        assertThat(user.getDeviceTokens()).isEmpty();
    }

}
