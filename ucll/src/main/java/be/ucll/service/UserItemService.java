package be.ucll.service;

import java.util.List;

import org.springframework.stereotype.Service;

import be.ucll.dto.UserItemResponseDTO;
import be.ucll.exception.DomainException;
import be.ucll.mapper.UserItemMapper;
import be.ucll.model.Item;
import be.ucll.model.User;
import be.ucll.model.UserItem;
import be.ucll.repository.ItemRepository;
import be.ucll.repository.UserItemRepository;
import jakarta.transaction.Transactional;

@Service
public class UserItemService {

    private final PushNotificationService pushNotificationService;
    
    private final UserItemRepository userItemRepository;

    private final ItemRepository itemRepository;

    public UserItemService(UserItemRepository userItemRepository, ItemRepository itemRepository, PushNotificationService pushNotificationService) {
        this.userItemRepository = userItemRepository;
        this.itemRepository = itemRepository;
        this.pushNotificationService = pushNotificationService;
    }

    public List<UserItem> getAllItemsFromUsers(String username) {
        if (username == null || username.isBlank()) {
            throw new DomainException("Username is needed to retrieve all items");
        }

        return userItemRepository.findByUser_Username(username);
    }

    @Transactional
    public List<UserItemResponseDTO> saveBatch(List<UserItemResponseDTO> dtos, User user) {
        List<UserItemResponseDTO> results = dtos.stream().map(dto -> {
            UserItem entity;
            if (dto.id() != null) {
                entity = userItemRepository.findById(dto.id())
                    .orElseThrow(() -> new DomainException("UserItem not found: " + dto.id()));

                if (!entity.getUser().getId().equals(user.getId())) {
                    throw new DomainException("Unauthorized update attempt.");
                }
            } else {
                entity = new UserItem();
                entity.setUser(user);

                Item baseItem;
                if (dto.itemId() != null) {
                    baseItem = itemRepository.findById(dto.itemId())
                    .orElseThrow(() -> new DomainException("Item template not found"));
                } else {
                    baseItem = itemRepository.findByNameContainingIgnoreCase(dto.itemName())
                        .orElseGet(() -> {
                            Item newItem = new Item();
                            newItem.setName(dto.itemName());
                            if (dto.type() != null) {
                                try {
                                    newItem.setType(Item.Type.valueOf(dto.type().toUpperCase()));
                                } catch (IllegalArgumentException e) {
                                    throw new DomainException("Invalid category: " + dto.type());
                                }
                            }
                            newItem.setOpenedRule(dto.openedRule() != null ? dto.openedRule() : 3);
                            return itemRepository.save(newItem);
                        });
                }
                entity.setItem(baseItem);
            }

            entity.setExpirationDate(dto.expirationDate());
            entity.setOpenedDate(dto.openedDate());
            entity.setOpenedRule(dto.openedRule());
            entity.setDescription(dto.description());

            return UserItemMapper.toDTO(userItemRepository.save(entity));
        }).toList();

        sendSuccessNotification(user, "Item(s) successfully saved!");

        return results;
    }

    private void sendSuccessNotification(User user, String message) {
        user.getDeviceTokens().forEach(deviceToken -> 
            pushNotificationService.sendToDevice(
                deviceToken.getToken(),
                message
            )
        );
    }

    @Transactional
    public void deleteUserItem(Long id, User user) {
        UserItem userItem = userItemRepository.findById(id)
            .orElseThrow(() -> new DomainException("Item not found with id: " + id));
        
        if (!userItem.getUser().getId().equals(user.getId())) {
            throw new DomainException("You do not have permission to delete this item.");
        }

        userItemRepository.delete(userItem);

        sendSuccessNotification(user, "Instance of item successfully removed!");
    }
}
