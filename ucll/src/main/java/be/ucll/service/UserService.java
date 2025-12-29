package be.ucll.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import be.ucll.dto.UserProfileDTO;
import be.ucll.exception.DomainException;
import be.ucll.model.Item;
import be.ucll.model.User;
import be.ucll.model.UserDeviceToken;
import be.ucll.model.UserItem;
import be.ucll.repository.ItemRepository;
import be.ucll.repository.UserDeviceTokenRepository;
import be.ucll.repository.UserItemRepository;
import be.ucll.repository.UserRepository;
import jakarta.transaction.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final UserItemRepository userItemRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDeviceTokenRepository userDeviceTokenRepository;

    public UserService(UserRepository userRepository, ItemRepository itemRepository, UserItemRepository userItemRepository, PasswordEncoder passwordEncoder, UserDeviceTokenRepository userDeviceTokenRepository) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.userItemRepository = userItemRepository;
        this.passwordEncoder = passwordEncoder;
        this.userDeviceTokenRepository = userDeviceTokenRepository;
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

    public void addDeviceToken(String username, String token, String deviceName) {
        User user = getUser(username);
        
        boolean exists = user.getDeviceTokens().stream()
          .anyMatch(t -> t.getToken().equals(token));

        if (!exists) {
            UserDeviceToken newToken = new UserDeviceToken(user, token, deviceName);
            user.addDeviceToken(newToken);
            userRepository.save(user);
        }
    }

    public List<String> getDeviceTokens(String username) {
        User user = getUser(username);

        return userDeviceTokenRepository.findAllByUserId(user.getId())
            .stream()
            .map(UserDeviceToken::getToken)
            .toList();
    }

    public void removeDeviceToken(String token) {
        UserDeviceToken deviceToken = userDeviceTokenRepository.findByToken(token)
          .orElseThrow(() -> new DomainException("Device token not found"));
        
          User user = deviceToken.getUser();
          user.removeDeviceToken(deviceToken);
          userRepository.save(user);
    }

    public User getUser(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new DomainException("User not found"));
    }

    public UserProfileDTO getUserProfile(User user) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        String formattedDate = user.getCreatedAt().format(formatter);

        int rescued = user.getItemsRescued();
        int currentExpired = userItemRepository.countExpiredItemsForUser(user);

        return new UserProfileDTO(
            user.getUsername(),
            user.getEmail(),
            user.getHousehold() != null ? user.getHousehold().getInviteCode() : "No Household",
            formattedDate,
            rescued,
            currentExpired
        );
    }
}
