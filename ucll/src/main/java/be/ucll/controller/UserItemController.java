package be.ucll.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import be.ucll.dto.UserItemResponseDTO;
import be.ucll.mapper.UserItemMapper;
import be.ucll.model.User;
import be.ucll.service.UserService;

@RequestMapping("/user-items")
@RestController
public class UserItemController {

    private final UserService userService;

    public UserItemController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserItemResponseDTO> getAllItemsFromUser(Authentication authentication) {
        User user = userService.getUser(authentication.getName());
        List<UserItemResponseDTO> ui = user.getUserItems()
            .stream()
            .map(UserItemMapper::toDTO)
            .toList();

        System.out.println(ui);
        return ui;
    }
}
