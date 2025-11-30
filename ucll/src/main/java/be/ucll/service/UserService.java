package be.ucll.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import be.ucll.exception.DomainException;
import be.ucll.model.Item;
import be.ucll.model.User;
import be.ucll.model.UserItem;
import be.ucll.repository.ItemRepository;
import be.ucll.repository.UserItemRepository;
import be.ucll.repository.UserRepository;
import jakarta.transaction.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final UserItemRepository userItemRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, ItemRepository itemRepository, UserItemRepository userItemRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.userItemRepository = userItemRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new DomainException("User not found with id: " + id));
    }

    public User registerUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username is already registered.");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email is already registered.");
        }

        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);

        return userRepository.save(user);
    }

    public User updateUser(Long id, User updatedUser) {
        User existingUser = getUserById(id);

        if (updatedUser.getEmail() != null) existingUser.setEmail(updatedUser.getEmail());
        if (updatedUser.getUsername() != null) existingUser.setUsername(updatedUser.getUsername());
        if (updatedUser.getPassword() != null) existingUser.setPassword(updatedUser.getPassword());

        return userRepository.save(existingUser);
    }

    public void deleteUser(Long id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }

    @Transactional
    public User addItemToUser(Long userId, Long itemId, LocalDate expirationDate) {
        if (expirationDate == null) {
            throw new DomainException("Expiration date is required.");
        }

        User user = getUserById(userId);
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new DomainException("Item not found with id: " + itemId));

        boolean alreadyLinked = user.getUserItems().stream()
            .anyMatch(ui -> ui.getItem().getId().equals(itemId));

        if (!alreadyLinked) {
            UserItem userItem = new UserItem(user, item, expirationDate);
            userItemRepository.save(userItem);
            user.addUserItem(userItem);
            item.addUserItem(userItem);
        }

        return userRepository.save(user);
    }

    public void removeItemFromUser(Long userId, Long itemId) {
        User user = getUserById(userId);
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new DomainException("Item not found with id: " + itemId));

        UserItem link = user.getUserItems().stream()
            .filter(ui -> ui.getItem().equals(item))
            .findFirst()
            .orElseThrow(() -> new DomainException("User does not have this item."));

        user.removeUserItem(link);
        item.removeUserItem(link);
        userItemRepository.delete(link);
    }
}
