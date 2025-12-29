package be.ucll.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import be.ucll.dto.UserDTO;
import be.ucll.dto.UserProfileDTO;
import be.ucll.model.User;
import be.ucll.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping
    public ResponseEntity<UserDTO> registerUser(@RequestBody User user) {
        User savedUser = userService.registerUser(user);

        String inviteCode = (savedUser.getHousehold() != null) ? savedUser.getHousehold().getInviteCode() : null;

        UserDTO userDTO = new UserDTO(savedUser.getId(), savedUser.getUsername(), savedUser.getEmail(), inviteCode);

        return ResponseEntity.status(201).body(userDTO);
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User user) {
        return userService.updateUser(id, user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{userId}/items/{itemId}/{expirationDate}")
    public User addItemToUser(@PathVariable Long userId, @PathVariable Long itemId, @PathVariable LocalDate expirationDate) {
        return userService.addItemToUser(userId, itemId, expirationDate);
    }

    @DeleteMapping("/{userId}/items/{itemId}")
    public ResponseEntity<Void> removeItemFromUser(@PathVariable Long userId, @PathVariable Long itemId) {
        userService.removeItemFromUser(userId, itemId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileDTO> getMyProfile(Authentication authentication) {
        User user = userService.getUser(authentication.getName());
        UserProfileDTO profile = userService.getUserProfile(user);
        return ResponseEntity.ok(profile);
    }
}
