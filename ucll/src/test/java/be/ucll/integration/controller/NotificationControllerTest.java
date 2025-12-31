package be.ucll.integration.controller;

import be.ucll.model.Notification;
import be.ucll.model.User;
import be.ucll.repository.NotificationRepository;
import be.ucll.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
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
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        notificationRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new User("testuser", "test@example.com", "Password123!");
        userRepository.save(testUser);
    }

    @Test
    @WithMockUser(username = "testuser")
    void getMyNotifications_happyPath() throws Exception {
        notificationRepository.save(new Notification(testUser, "Alert 1", "Body 1", null));
        notificationRepository.save(new Notification(testUser, "Alert 2", "Body 2", null));

        mockMvc.perform(get("/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", is("Alert 2")))
                .andExpect(jsonPath("$[1].title", is("Alert 1")));
    }

    @Test
    @WithMockUser(username = "testuser")
    void markAsRead_happyPath() throws Exception {
        Notification n = new Notification(testUser, "T", "B", null);
        notificationRepository.save(n);

        mockMvc.perform(put("/notifications/" + n.getId() + "/read"))
                .andExpect(status().isOk());

        Notification updated = notificationRepository.findById(n.getId()).orElseThrow();
        assertThat(updated.getIsRead()).isTrue();
    }

    @Test
    @WithMockUser(username = "testuser")
    void markAllAsRead_happyPath() throws Exception {
        notificationRepository.save(new Notification(testUser, "T1", "B1", null));
        notificationRepository.save(new Notification(testUser, "T2", "B2", null));

        mockMvc.perform(put("/notifications/read-all"))
                .andExpect(status().isOk());

        long unreadCount = notificationRepository.countByUser_UsernameAndIsReadFalse("testuser");
        assertThat(unreadCount).isZero();
    }

    @Test
    @WithMockUser(username = "testuser")
    void deleteNotification_happyPath() throws Exception {
        Notification n = new Notification(testUser, "To Delete", "B", null);
        notificationRepository.save(n);

        mockMvc.perform(delete("/notifications/" + n.getId()))
                .andExpect(status().isNoContent());

        assertThat(notificationRepository.existsById(n.getId())).isFalse();
    }

    @Test
    @WithMockUser(username = "testuser")
    void getUnreadCount_happyPath() throws Exception {
        Notification n = new Notification(testUser, "T", "B", null);
        notificationRepository.save(n);

        mockMvc.perform(get("/notifications/unread-count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(1)));
    }
}