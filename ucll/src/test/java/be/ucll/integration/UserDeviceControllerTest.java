package be.ucll.integration;

import be.ucll.model.User;
import be.ucll.model.UserDeviceToken;
import be.ucll.repository.UserDeviceTokenRepository;
import be.ucll.repository.UserRepository;
import jakarta.transaction.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessaging;
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

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@TestPropertySource(properties = {
        "jwt.secret=Y8r3mP9wQ2tF6sV1xB7eH4kN0uJ5cR8Z",
        "jwt.expiration=3600000"
})
public class UserDeviceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDeviceTokenRepository userDeviceTokenRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;

    @BeforeEach
    void setUp() {
        userDeviceTokenRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new User("testuser", "test@example.com", "Password123!");
        userRepository.save(testUser);
    }

    // POSTS

    @Test
    @WithMockUser(username = "testuser")
    void saveDeviceToken_happyPath() throws Exception {
        // Given
        Map<String, String> payload = Map.of(
                "token", "my-token-123",
                "deviceName", "Iphone 13");

        // When
        mockMvc.perform(post("/users/device-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                // Then
                .andExpect(status().isOk());

        var tokens = userDeviceTokenRepository.findAll();
        assertThat(tokens).hasSize(1);
        assertThat(tokens.get(0).getToken()).isEqualTo("my-token-123");
        assertThat(tokens.get(0).getUser().getUsername()).isEqualTo("testuser");
    }

    @Test
    @WithMockUser(username = "testuser")
    void saveDeviceToken_missingToken_unhappyPath() throws Exception {
        // Given
        Map<String, String> payload = Map.of(
                "deviceName", "Iphone 13");

        // When
        mockMvc.perform(post("/users/device-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))

                // Then
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "testuser")
    void removeDeviceToken_happyPath() throws Exception {
        // Given
        UserDeviceToken existingToken = new UserDeviceToken(testUser, "token-to-delete", "Iphone 13");
        testUser.addDeviceToken(existingToken);
        userRepository.save(testUser);

        assertThat(userDeviceTokenRepository.findAll()).hasSize(1);

        Map<String, String> payload = Map.of("token", "token-to-delete");

        // When
        mockMvc.perform(delete("/users/device-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))

                // Then
                .andExpect(status().isOk());

        assertThat(userDeviceTokenRepository.findAll().isEmpty());
    }

    @Test
    @WithMockUser(username = "testuser")
    void removeDeviceToken_badRequest_unhappyPath() throws Exception {
        // Given
        Map<String, String> payload = Map.of("token", "");

        // When
        mockMvc.perform(delete("/users/device-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))

                // Then
                .andExpect(status().isBadRequest());
    }
}
