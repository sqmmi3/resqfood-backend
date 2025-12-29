package be.ucll.integration.controller;

import be.ucll.model.User;
import be.ucll.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "jwt.secret=Y8r3mP9wQ2tF6sV1xB7eH4kN0uJ5cR8Z",
        "jwt.expiration=3600000"
})
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        // Create user for login tests
        User user = new User("testuser", "test@example.com", passwordEncoder.encode("Password123!"));
        userRepository.save(user);
    }

    @Test
    void login_happyPath() throws Exception {
        // Given
        record LoginRequest(String username, String password) {}
        var loginRequest = new LoginRequest("testuser", "Password123!");

        // When
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()));

    }

    @Test
    void login_wrongPassword_unhappyPath() throws Exception {
        // Given
        record LoginRequest(String username, String password) {}

        var loginRequest = new LoginRequest("testuser", "WrongPass");

        // When
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                // Then
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_userNotFound_unhappyPath() throws Exception {
        // Given
        record LoginRequest(String username, String password) {}
        var loginRequest = new LoginRequest("fakeuser", "Password123!");

        // When
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                // Then
                .andExpect(status().isUnauthorized());
    }
}
