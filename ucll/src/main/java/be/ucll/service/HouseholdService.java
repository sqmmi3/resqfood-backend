package be.ucll.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import be.ucll.exception.DomainException;
import be.ucll.model.Household;
import be.ucll.model.User;
import be.ucll.repository.HouseholdRepository;
import be.ucll.repository.UserRepository;
import jakarta.transaction.Transactional;

@Service
public class HouseholdService {
  private final HouseholdRepository householdRepository;
  private final UserRepository userRepository;

  public HouseholdService(HouseholdRepository householdRepository, UserRepository userRepository) {
    this.householdRepository = householdRepository;
    this.userRepository = userRepository;
  }

  @Transactional
  public Household createHousehold(User user) {
    if (user.getHousehold() != null) {
      throw new DomainException("You are already in a household.");
    }

    String code = UUID.randomUUID().toString().substring(0, 6).toUpperCase();

    Household household = new Household(code);
    Household savedHousehold = householdRepository.save(household);

    user.setHousehold(savedHousehold);
    userRepository.save(user);

    return savedHousehold;
  }

  @Transactional
  public Household joinHousehold(String inviteCode, User user) {
    if (user.getHousehold() != null) {
        throw new DomainException("You already are in a household.");
    }

    Household household = householdRepository.findByInviteCode(inviteCode.toUpperCase())
        .orElseThrow(() -> new DomainException("Household with code " + inviteCode + " not found."));

    user.setHousehold(household);
    userRepository.save(user);

    return household;
  }

  public void leaveHousehold(User user) {
    Household household = user.getHousehold();

    if (household == null) {
      throw new DomainException("User is not part of any household.");
    }

    user.setHousehold(null);
    userRepository.save(user);

    household.getMembers().remove(user);

    if (household.getMembers().isEmpty()) {
      householdRepository.delete(household);
    } else {
      householdRepository.save(household);
    }
  }

  public List<Household> getAllUsersFromHousehold() {
    return householdRepository.findAll();
  }
}
