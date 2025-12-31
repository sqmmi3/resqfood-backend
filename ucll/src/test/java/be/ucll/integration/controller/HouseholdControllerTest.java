package be.ucll.integration.controller;

import be.ucll.model.Household;
import be.ucll.model.User;
import be.ucll.repository.HouseholdRepository;
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
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
class HouseholdControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HouseholdRepository householdRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        householdRepository.deleteAll();

        testUser = new User("testuser", "test@example.com", "Password123!");
        userRepository.save(testUser);
    }

    @Test
    @WithMockUser(username = "testuser")
    void createHousehold_happyPath() throws Exception {
        mockMvc.perform(post("/households/create"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message", is("Household created successfully")))
                .andExpect(jsonPath("$.inviteCode").exists());

        User updatedUser = userRepository.findByUsername("testuser").orElseThrow();
        assertThat(updatedUser.getHousehold()).isNotNull();
        assertThat(updatedUser.getHousehold().getInviteCode()).hasSize(6);
    }

    @Test
    @WithMockUser(username = "testuser")
    void joinHousehold_happyPath() throws Exception {
        Household existingHousehold = new Household("JOINME");
        householdRepository.save(existingHousehold);

        mockMvc.perform(post("/households/join/JOINME"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.inviteCode", is("JOINME")));

        User updatedUser = userRepository.findByUsername("testuser").orElseThrow();
        assertThat(updatedUser.getHousehold().getInviteCode()).isEqualTo("JOINME");
    }

    @Test
    @WithMockUser(username = "testuser")
    void getMyHouseholdDetails_happyPath() throws Exception {
        Household household = new Household("HOME01");
        householdRepository.save(household);
        testUser.setHousehold(household);
        userRepository.save(testUser);
        household.addMember(testUser);

        mockMvc.perform(get("/households/my-household"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.inviteCode", is("HOME01")))
                .andExpect(jsonPath("$.members[0]", is("testuser")));
    }

    @Test
    @WithMockUser(username = "testuser")
    void leaveHousehold_happyPath() throws Exception {
        Household household = new Household("EXIT01");
        householdRepository.save(household);
        testUser.setHousehold(household);
        userRepository.save(testUser);

        mockMvc.perform(post("/households/leave"))
                .andExpect(status().isOk());

        User updatedUser = userRepository.findByUsername("testuser").orElseThrow();
        assertThat(updatedUser.getHousehold()).isNull();
        
        assertThat(householdRepository.findByInviteCode("EXIT01")).isEmpty();
    }
}