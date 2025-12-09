package be.ucll.service;

import java.util.List;

import org.springframework.stereotype.Service;

import be.ucll.exception.DomainException;
import be.ucll.model.UserItem;
import be.ucll.repository.UserItemRepository;

@Service
public class UserItemService {
    
    private final UserItemRepository userItemRepository;

    public UserItemService(UserItemRepository userItemRepository) {
        this.userItemRepository = userItemRepository;
    }

    public List<UserItem> getAllItemsFromUsers(String username) {
        if (username == null || username.isBlank()) {
            throw new DomainException("Username is needed to retrieve all items");
        }

        return userItemRepository.findByUser_Username(username);
    }
}
