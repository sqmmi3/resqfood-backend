package be.ucll.integration.controller;

import be.ucll.dto.UserItemResponseDTO;
import be.ucll.model.Item;
import be.ucll.model.User;
import be.ucll.model.UserItem;
import be.ucll.repository.ItemRepository;
import be.ucll.repository.UserItemRepository;
import be.ucll.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessaging;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@TestPropertySource(properties = {
        "jwt.secret=Y8r3mP9wQ2tF6sV1xB7eH4kN0uJ5cR8Z",
        "jwt.expiration=3600000"
})
public class UserItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserItemRepository userItemRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private Item testItem;

    @BeforeEach
    void setUp() {
        userItemRepository.deleteAll();
        userRepository.deleteAll();
        itemRepository.deleteAll();

        testUser = new User("testuser", "test@example.com", "Password123!");
        userRepository.save(testUser);

        testItem = new Item("Banana", Item.Type.FRUIT);
        itemRepository.save(testItem);
    }

    @Test
    @WithMockUser(username = "testuser")
    void getAllItemsFromUser_happyPath() throws Exception {
        // Given
        UserItem link = new UserItem(testUser, testItem, LocalDate.now().plusDays(5));
        testUser.addUserItem(link);
        userItemRepository.save(link);

        // When
        mockMvc.perform(get("/user-items"))
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].itemName", is("Banana")));
    }

    @Test
    @WithMockUser(username = "testuser")
    void updateBatch_create_happyPath() throws Exception {
        // Given
        UserItemResponseDTO newDto = new UserItemResponseDTO(
                null,
                testItem.getId(),
                "Banana",
                "Fruit",
                LocalDate.now().plusDays(7),
                null,
                3,
                "Description");

        // When
        mockMvc.perform(put("/user-items/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(newDto))))
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].description", is("Description")));

        List<UserItem> items = userItemRepository.findAll();
        assertThat(items).hasSize(1);
        assertThat(items.get(0).getUser().getUsername()).isEqualTo("testuser");
    }

    @Test
    @WithMockUser(username = "testuser")
    void updateBatch_update_happyPath() throws Exception {
        // Given
        UserItem existing = new UserItem(testUser, testItem, LocalDate.now().plusDays(5));
        userItemRepository.save(existing);

        UserItemResponseDTO updateDto = new UserItemResponseDTO(
                existing.getId(),
                testItem.getId(),
                "Banana",
                "Fruit",
                LocalDate.now().plusDays(5),
                LocalDate.now(),
                3,
                "Updated Description");

        // When
        mockMvc.perform(put("/user-items/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(updateDto))))

                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description", is("Updated Description")));

        UserItem updatedItem = userItemRepository.findById(existing.getId()).orElseThrow();
        assertThat(updatedItem.getDescription()).isEqualTo("Updated Description");
        assertThat(updatedItem.getOpenedDate()).isNotNull();
    }

    @Test
    @WithMockUser(username = "testuser")
    void deleteUserItem_happyPath() throws Exception {
        // Given
        UserItem existing = new UserItem(testUser, testItem, LocalDate.now().plusDays(5));
        userItemRepository.save(existing);

        // When
        mockMvc.perform(delete("/user-items/" + existing.getId()))

                // Then
                .andExpect(status().isNoContent());

        assertThat(userItemRepository.findAll().isEmpty());
    }

    @Test
    @WithMockUser
    void deleteUserItem_noOwner_unhappyPath() throws Exception {
        // Given
        User otherUser = new User("other", "other@example.com", "Password123!");
        userRepository.save(otherUser);

        UserItem otherItem = new UserItem(otherUser, testItem, LocalDate.now());
        userItemRepository.save(otherItem);

        // When
        mockMvc.perform(delete("/user-items/" + otherItem.getId()))

                // Then
                .andExpect(status().isInternalServerError());
    }

}
