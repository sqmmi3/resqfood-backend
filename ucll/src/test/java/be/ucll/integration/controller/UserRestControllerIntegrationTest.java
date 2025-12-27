package be.ucll.integration.controller;

import be.ucll.model.Item;
import be.ucll.model.User;
import be.ucll.model.UserItem;
import be.ucll.repository.ItemRepository;
import be.ucll.repository.UserItemRepository;
import be.ucll.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

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
import static org.hamcrest.Matchers.notNullValue;
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
public class UserRestControllerIntegrationTest {

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

    @BeforeEach
    void setUp() {
        userItemRepository.deleteAll();
        userRepository.deleteAll();
        itemRepository.deleteAll();
    }

    @Test
    void registerUser_happyPath() throws Exception {
        // Given
        User newUser = new User("newUser", "new@ucll.be", "Password123!");

        // When
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)))

                // Then
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username", is("newUser")))
                .andExpect(jsonPath("$.email", is("new@ucll.be")))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getAllUsers_happyPath() throws Exception {
        // Given
        userRepository.save(new User("user1", "user1@ucll.be", "Pass123!"));
        userRepository.save(new User("user2", "user2@ucll.be", "Pass123!"));

        // When
        mockMvc.perform(get("/users"))

                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @WithMockUser
    void getUserById_happyPath() throws Exception {
        // Given
        User saved = userRepository.save(new User("target", "target@ucll.be", "Pass123!"));

        // When
        mockMvc.perform(get("/users/" + saved.getId()))

                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("target")));
    }

    @Test
    @WithMockUser
    void updateUser_happyPath() throws Exception {
        // Given
        User original = userRepository.save(new User("oldName", "old@ucll.be", "Pass123!"));

        User updates = new User("newName", "new@ucll.be", "NewPass123!");

        // When
        mockMvc.perform(put("/users/" + original.getId())
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updates)))

                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("newName")));

        User dbUser = userRepository.findById(original.getId()).orElseThrow();
        assertThat(dbUser.getEmail()).isEqualTo("new@ucll.be");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_happyPath() throws Exception {
        // Given
        User toDelete = userRepository.save(new User("deleteMe", "del@ucll.be", "Pass123!"));

        // When
        mockMvc.perform(delete("/users/" + toDelete.getId()))

                // Then
                .andExpect(status().isNoContent());

        assertThat(userRepository.findById(toDelete.getId())).isEmpty();
    }

    @Test
    @WithMockUser
    void addItemToUser_happyPath() throws Exception {
        // Given
        User user = userRepository.save(new User("linkUser", "link@ucll.be", "Pass123!"));
        Item item = itemRepository.save(new Item("Milk", Item.Type.DAIRY));
        LocalDate expiry = LocalDate.now().plusDays(10);

        // When
        mockMvc.perform(put("/users/" + user.getId() + "/items/" + item.getId() + "/" + expiry))

                // Then
                .andExpect(status().isOk());

        List<UserItem> links = userItemRepository.findByUser_Username("linkUser");
        assertThat(links).hasSize(1);
        assertThat(links.get(0).getItem().getName()).isEqualTo("Milk");
        assertThat(links.get(0).getExpirationDate()).isEqualTo(expiry);
    }

    @Test
    @WithMockUser
    void removeItemFromUser_happyPath() throws Exception {
        // Given
        User user = new User("unlinkUser", "unlink@ucll.be", "Pass123!");
        userRepository.save(user);

        Item item = new Item("Bread", Item.Type.GRAIN);
        itemRepository.save(item);

        UserItem link = new UserItem(user, item, LocalDate.now());
        user.addUserItem(link);
        item.addUserItem(link);

        userItemRepository.save(link);

        // When
        mockMvc.perform(delete("/users/" + user.getId() + "/items/" + item.getId())
                .with(csrf()))

                // Then
                .andExpect(status().isNoContent());

        assertThat(userItemRepository.findAll()).isEmpty();
    }
}
