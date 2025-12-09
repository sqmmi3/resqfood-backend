package be.ucll.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import be.ucll.model.UserItem;
import be.ucll.service.UserItemService;

@RequestMapping("/user-items")
@RestController
public class UserItemController {

    private final UserItemService userItemService;

    public UserItemController(UserItemService userItemService) {
        this.userItemService = userItemService;
    }

    @GetMapping
    public List<UserItem> getAllItemsFromUser(Authentication authentication) {
        String username = authentication.getName();
        return userItemService.getAllItemsFromUsers(username);
    }
}
