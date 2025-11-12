package be.ucll.service;

import java.util.List;

import org.springframework.stereotype.Service;

import be.ucll.model.Item;
import be.ucll.model.User;
import be.ucll.repository.ItemRepository;
import be.ucll.repository.UserRepository;

@Service
public class UserService {
    final UserRepository userRepository;
    final ItemRepository itemRepository;

    public UserService(UserRepository userRepository, ItemRepository itemRepository) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User updateUser(Long id, User updatedUser) {
        User existingUser = getUserById(id);

        if (updatedUser.getEmail() != null) {
            existingUser.setEmail(updatedUser.getEmail());
        }

        if (updatedUser.getUsername() != null) {
            existingUser.setUsername(updatedUser.getUsername());
        }

        if (updatedUser.getPassword() != null) {
            existingUser.setPassword(updatedUser.getPassword());
        }

        return userRepository.save(existingUser);
    }

    public void deleteUser(Long id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }

    public User addItemToUser(Long userId, Long itemId) {
        User user = getUserById(userId);
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new RuntimeException("Item not found with id: " + itemId));

        user.addItem(item);
        return userRepository.save(user);
    }

    public void removeItemFromUser(Long userId, Long itemId) {
        User user = getUserById(userId);
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new RuntimeException("Item not found with id: " + itemId));

        user.removeItem(item);
        userRepository.save(user);
    }
}
