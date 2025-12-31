package be.ucll.unit.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import be.ucll.model.Household;
import be.ucll.model.User;

@ExtendWith(MockitoExtension.class)
class HouseholdTest {
    
    @Mock
    private User mockUser;

    @Test
    void constructor_ShouldSetInvite() {
        String code = "ABCDEF";
        Household household = new Household(code);

        assertThat(household.getInviteCode()).isEqualTo(code);
        assertThat(household.getMembers()).isEmpty();
    }

    @Test
    void addMember_ShouldEstablishBidirectionalRelationship() {
        Household household = new Household("CODE12");
        household.addMember(mockUser);

        assertThat(household.getMembers()).containsExactly(mockUser);
        verify(mockUser).setHousehold(household);
    }

    @Test
    void removeMember_ShouldBreakRelationship() {
        Household household = new Household("CODE12");
        household.addMember(mockUser);

            household.removeMember(mockUser);

        assertThat(household.getMembers()).isEmpty();
        verify(mockUser).setHousehold(null);
    }
}
