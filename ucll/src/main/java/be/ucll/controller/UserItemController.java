package be.ucll.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import be.ucll.dto.UserItemResponseDTO;
import be.ucll.mapper.UserItemMapper;
import be.ucll.model.User;
import be.ucll.service.UserItemService;
import be.ucll.service.UserService;

@RequestMapping("/user-items")
@RestController
public class UserItemController {

    private final UserService userService;

    private final UserItemService userItemService;

    public UserItemController(UserService userService, UserItemService userItemService) {
        this.userService = userService;
        this.userItemService = userItemService;
    }

    @GetMapping
    public List<UserItemResponseDTO> getAllItemsFromUser(Authentication authentication) {
        User currentUser = userService.getUser(authentication.getName());
        return currentUser.getUserItems()
            .stream()
            .map(UserItemMapper::toDTO)
            .toList();
    }

    @PutMapping("/batch")
    public ResponseEntity<List<UserItemResponseDTO>> saveBatch(@RequestBody List<UserItemResponseDTO> dtos, Authentication authentication) {
        User currentUser = userService.getUser(authentication.getName());
        List<UserItemResponseDTO> result = userItemService.saveBatch(dtos, currentUser);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserItem(@PathVariable Long id, Authentication authentication) {
        User currentUser = userService.getUser(authentication.getName());
        userItemService.deleteUserItem(id, currentUser);
        return ResponseEntity.noContent().build();
    }
}
