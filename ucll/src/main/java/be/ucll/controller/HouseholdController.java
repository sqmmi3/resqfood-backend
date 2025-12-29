package be.ucll.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import be.ucll.dto.HouseholdResponseDTO;
import be.ucll.model.Household;
import be.ucll.model.User;
import be.ucll.service.HouseholdService;
import be.ucll.service.UserService;

@RestController
@RequestMapping("/households")
public class HouseholdController {
  private final HouseholdService householdService;
  private final UserService userService;

  public HouseholdController(HouseholdService householdService, UserService userService) {
    this.householdService = householdService;
    this.userService = userService;
  }

  @PostMapping("/create")
  public ResponseEntity<HouseholdResponseDTO> createHousehold(Authentication authentication) {
    User user = userService.getUser(authentication.getName());
    Household household = householdService.createHousehold(user);

    HouseholdResponseDTO response = new HouseholdResponseDTO(
      "Household created successfully",
      household.getInviteCode()
    );

    return ResponseEntity.status(201).body(response);
  }

  @PostMapping("/join/{code}")
  public ResponseEntity<HouseholdResponseDTO> joinHousehold(@PathVariable String code, Authentication authentication) {
    User user = userService.getUser(authentication.getName());
    Household household = householdService.joinHousehold(code, user);

    HouseholdResponseDTO response = new HouseholdResponseDTO(
      user.getUsername() + "successfully joined household " + household.getInviteCode(),
      household.getInviteCode()
    );

    return ResponseEntity.status(200).body(response);
  }

  @PostMapping("/leave")
  public ResponseEntity<Void> leaveHousehold(Authentication authentication) {
    User user = userService.getUser(authentication.getName());
    householdService.leaveHousehold(user);
    return ResponseEntity.status(200).build();
  }

  @GetMapping
  public List<Household> getAllHouseholds() {
    return householdService.getAllUsersFromHousehold();
  }
}
