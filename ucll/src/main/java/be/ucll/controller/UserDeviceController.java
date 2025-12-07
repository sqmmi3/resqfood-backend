package be.ucll.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import be.ucll.service.UserService;

@RestController
@RequestMapping("/users")
public class UserDeviceController {
  private final UserService userService;

  public UserDeviceController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping("/device-token")
  public ResponseEntity<Void> saveDeviceToken(@RequestBody Map<String, String> body, Authentication authentication) {
    String token = body.get("token");
    String deviceName = body.getOrDefault("deviceName", "unknown");

    if (token == null || token.isBlank()) return ResponseEntity.badRequest().build();

    String username = authentication.getName();
    userService.addDeviceToken(username, token, deviceName);

    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/device-token")
  public ResponseEntity<Void> removeDeviceToken(
    @RequestBody Map<String, String> body
  ) {
    String token = body.get("token");
    if (token == null || token.isBlank()) return ResponseEntity.badRequest().build();

    userService.removeDeviceToken(token);
    return ResponseEntity.ok().build();
  }
}
