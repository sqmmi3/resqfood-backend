package be.ucll.unit.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import be.ucll.dto.UserItemResponseDTO;
import be.ucll.exception.DomainException;
import be.ucll.model.Item;
import be.ucll.model.User;
import be.ucll.model.UserDeviceToken;
import be.ucll.model.UserItem;
import be.ucll.repository.ItemRepository;
import be.ucll.repository.UserItemRepository;
import be.ucll.service.PushNotificationService;
import be.ucll.service.UserItemService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserItemServiceTest {

    // Global given
    private final String validUsername = "TestUser";
    private final Long validUserId = 10L;
    private final Long validItemId = 5L;
    private final Long validUserItemId = 100L;

    @Mock
    private UserItemRepository userItemRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private PushNotificationService pushNotificationService;

    @InjectMocks
    private UserItemService userItemService;

    // Helper to create user with ID
    private User createMockUser(Long id) {
        User u = mock(User.class);
        when(u.getId()).thenReturn(id);
        return u;
    }

    @Test
    void getAllItemsFromUsers_happyPath() {
        // Given
        UserItem ui1 = new UserItem();
        when(userItemRepository.findByUser_Username(validUsername)).thenReturn(List.of(ui1));

        // When
        List<UserItem> result = userItemService.getAllItemsFromUsers(validUsername);

        // Then
        assertThat(result).hasSize(1);
        verify(userItemRepository).findByUser_Username(validUsername);
    }

    @Test
    void getAllItemsFromUsers_unhappyPath() {
        // When / Then
        assertThatThrownBy(() -> userItemService.getAllItemsFromUsers(null))
                .isInstanceOf(DomainException.class)
                .hasMessage("Username is needed to retrieve all items");

        assertThatThrownBy(() -> userItemService.getAllItemsFromUsers(" "))
                .isInstanceOf(DomainException.class)
                .hasMessage("Username is needed to retrieve all items");
    }

    @Test
    void saveBatch_createNew_happyPath() {
        // Given
        User user = new User(validUsername, "email", "password");
        // Add device token to check notif logic
        UserDeviceToken token = new UserDeviceToken(user, "token123", "device");
        user.addDeviceToken(token);

        Item item = new Item("Banana", Item.Type.FRUIT);

        // DTO
        UserItemResponseDTO newDto = new UserItemResponseDTO(
                null, // ID = null -> create
                validItemId,
                "Banana",
                "Fruit",
                LocalDate.now().plusDays(5),
                LocalDate.now(),
                3,
                "Desc");

        when(itemRepository.findById(validItemId)).thenReturn(Optional.of(item));
        when(userItemRepository.save(any(UserItem.class))).thenAnswer(i -> {
            UserItem saved = i.getArgument(0);
            saved.setId(validUserItemId);
            return saved;
        });

        // When
        List<UserItemResponseDTO> result = userItemService.saveBatch(List.of(newDto), user);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(validUserItemId);

        // Verify if notif is sent
        verify(pushNotificationService).sendToDevice(eq("token123"), contains("Item(s) successfully updated!"));
    }

    @Test
    void saveBatch_updateExisiting_happyPath() {
        // Given
        User user = createMockUser(validUserId);
        UserItem existingItem = new UserItem();
        existingItem.setId(validUserItemId);
        existingItem.setUser(user);

        Item mockItem = new Item("Banana", Item.Type.FRUIT);
        existingItem.setItem(mockItem);

        // DTO
        UserItemResponseDTO updateDTO = new UserItemResponseDTO(
                validUserItemId,
                validItemId,
                "Banana",
                "FRUIT",
                LocalDate.now().plusDays(10),
                LocalDate.now(),
                3,
                "New Desc"

        );

        when(userItemRepository.findById(validUserItemId)).thenReturn(Optional.of(existingItem));
        when(userItemRepository.save(any(UserItem.class))).thenReturn(existingItem);

        // When
        List<UserItemResponseDTO> result = userItemService.saveBatch(List.of(updateDTO), user);

        // Then
        assertThat(result).hasSize(1);
        verify(userItemRepository).findById(validUserItemId);
        verify(userItemRepository).save(existingItem);
        // Check if it was updated
        assertThat(existingItem.getDescription()).isEqualTo("New Desc");
    }

    @Test
    void saveBatch_notFound_unhappyPath() {
        // Given
        User user = new User("TestUser", "test@example.com", "Pass123!");
        UserItemResponseDTO updateDto = new UserItemResponseDTO(
                999L,
                validItemId,
                "Banana",
                "FRUIT",
                LocalDate.now().plusDays(5),
                LocalDate.now(),
                3,
                "Desc");

        when(userItemRepository.findById(999L)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> userItemService.saveBatch(List.of(updateDto), user))
                .isInstanceOf(DomainException.class).hasMessageContaining("UserItem not found");
    }

    @Test
    void saveBatch_unauthorized_unhappyPath() {
        // Given
        User currentUser = createMockUser(validUserId);
        User otherUser = createMockUser(20L);

        UserItem exisitingItem = new UserItem();
        exisitingItem.setUser(otherUser);

        UserItemResponseDTO updateDto = new UserItemResponseDTO(
                validUserItemId,
                validItemId,
                "Banana",
                "FRUIT",
                LocalDate.now().plusDays(5),
                LocalDate.now(),
                3,
                "Desc");

        when(userItemRepository.findById(validUserItemId)).thenReturn(Optional.of(exisitingItem));

        // When / Then
        assertThatThrownBy(() -> userItemService.saveBatch(List.of(updateDto), currentUser))
                .isInstanceOf(DomainException.class)
                .hasMessage("Unauthorized update attempt.");
    }

    @Test
    void deleteUserItem_happyPath() {
        // Given
        User user = createMockUser(validUserId);

        when(user.getDeviceTokens()).thenReturn(List.of());

        UserItem userItem = new UserItem();
        userItem.setUser(user);

        when(userItemRepository.findById(validUserItemId)).thenReturn(Optional.of(userItem));

        // When
        userItemService.deleteUserItem(validUserItemId, user);

        // Then
        verify(userItemRepository).delete(userItem);
    }

    @Test
    void deleteUserITem_unauthorized_unhappyPath() {
        // Given
        User currentUser = createMockUser(validUserId);
        User otherUser = createMockUser(20L);

        UserItem exisitingItem = new UserItem();
        exisitingItem.setUser(otherUser);

        when(userItemRepository.findById(validUserItemId)).thenReturn(Optional.of(exisitingItem));

        // When / Then
        assertThatThrownBy(() -> userItemService.deleteUserItem(validUserItemId, currentUser))
                .isInstanceOf(DomainException.class)
                .hasMessage("You do not have permission to delete this item.");

        verify(userItemRepository, never()).delete(any());
    }

    @Test
    void deleteUserItem_notFound_unhappyPath() {
        // Given
        User user = new User("username", "a@b.c", "pass123!");
        when(userItemRepository.findById(validUserItemId)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> userItemService.deleteUserItem(validUserItemId, user))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("Item not found");
    }
}
