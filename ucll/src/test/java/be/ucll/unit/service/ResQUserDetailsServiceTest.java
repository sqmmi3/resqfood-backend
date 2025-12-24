package be.ucll.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import be.ucll.model.User;
import be.ucll.repository.UserRepository;
import be.ucll.service.ResQUserDetailsService;

@ExtendWith(MockitoExtension.class)
public class ResQUserDetailsServiceTest {

    // Global given
    private final String validUsername = "TestUser";
    private final String validEmail = "test@example.com";
    private final String validPassword = "Password123!";

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ResQUserDetailsService userDetailsService;

    @Test
    void loadUserByUsername_happyPath() {
        // Given
        User domainUser = new User(validUsername, validEmail, validPassword);
        when(userRepository.findByUsername(validUsername)).thenReturn(Optional.of(domainUser));

        // When
        UserDetails result = userDetailsService.loadUserByUsername(validUsername);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo(validUsername);
        assertThat(result.getPassword()).isEqualTo(validPassword);

        // Verify authorities
        // Hardcoded "USER" in service
        assertThat(result.getAuthorities()).hasSize(1).anyMatch(a -> a.getAuthority().equals("USER"));
    }

    @Test
    void loadUserByUsername_unhappyPath() {
        // Given
        when(userRepository.findByUsername(validUsername)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(validUsername))
                .isInstanceOf(UsernameNotFoundException.class).hasMessage("User not found.");
    }

}
