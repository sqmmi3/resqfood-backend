package be.ucll.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import be.ucll.exception.DomainException;
import be.ucll.model.Household;
import be.ucll.model.User;
import be.ucll.repository.HouseholdRepository;
import be.ucll.repository.UserRepository;
import be.ucll.service.HouseholdService;

@ExtendWith(MockitoExtension.class)
class HouseholdServiceTest {
    
    @Mock
    private HouseholdRepository householdRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private HouseholdService householdService;

    @Mock
    private User mockUser;

    @Test
    void createHousehold_ShouldSucceed_WhenUserHasNoHousehold() {
        when(mockUser.getHousehold()).thenReturn(null);
        when(householdRepository.save(any(Household.class))).thenAnswer(i -> i.getArguments()[0]);

        Household result = householdService.createHousehold(mockUser);

        assertThat(result.getInviteCode()).hasSize(6);
        verify(mockUser).setHousehold(result);
        verify(userRepository).save(mockUser);
        verify(householdRepository).save(any(Household.class));
    }

    @Test
    void createHousehold_ShouldThrowException_WhenUserAlreadyInHousehold() {
        when(mockUser.getHousehold()).thenReturn(new Household("CODE12"));
        
        assertThatThrownBy(() -> householdService.createHousehold(mockUser))
            .isInstanceOf(DomainException.class)
            .hasMessage("You are already in a household.");

        verify(householdRepository, never()).save(any());
    }

    @Test
    void joinHousehold_ShouldSucceed_WhenCodeIsValid() {
        String code = "JOINME";
        Household household = new Household(code);
        when(mockUser.getHousehold()).thenReturn(null);
        when(householdRepository.findByInviteCode(code)).thenReturn(Optional.of(household));

        Household result = householdService.joinHousehold(code, mockUser);

        assertThat(result).isEqualTo(household);
        verify(mockUser).setHousehold(household);
        verify(userRepository).save(mockUser);
    }

    @Test
    void joinHousehold_ShouldThrowException_WhenCodeNotFound() {
        String code = "WRONG1";
        when(mockUser.getHousehold()).thenReturn(null);
        when(householdRepository.findByInviteCode(code)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> householdService.joinHousehold(code, mockUser))
            .isInstanceOf(DomainException.class)
            .hasMessage("Household with code " + code + " not found.");
    }

    @Test
    void leaveHousehold_ShouldDeleteHousehold_WhenLastMemberLeaves() {
        Household household = spy(new Household("LEAVE1"));
        ArrayList<User> members = new ArrayList<>();
        members.add(mockUser);

        when(mockUser.getHousehold()).thenReturn(household);
        when(household.getMembers()).thenReturn(members);

        householdService.leaveHousehold(mockUser);

        verify(mockUser).setHousehold(null);
        verify(userRepository).save(mockUser);
        verify(householdRepository).delete(household);
    }

    @Test
    void leaveHousehold_ShouldSaveHousehold_WhenOtherMembersRemain() {
        Household household = spy(new Household("STAY1"));
        User otherUser = mock(User.class);
        ArrayList<User> members = new ArrayList<>();
        members.add(mockUser);
        members.add(otherUser);

        when(mockUser.getHousehold()).thenReturn(household);
        when(household.getMembers()).thenReturn(members);

        householdService.leaveHousehold(mockUser);

        verify(householdRepository).save(household);
        verify(householdRepository, never()).delete(any());
    }

    @Test
    void leaveHousehold_ShouldThrowException_WhenUserHasNoHousehold() {
        when(mockUser.getHousehold()).thenReturn(null);

        assertThatThrownBy(() -> householdService.leaveHousehold(mockUser))
            .isInstanceOf(DomainException.class)
            .hasMessage("User is not part of any household.");
    }
}
