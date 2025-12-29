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

// TODO: Item name in notification?

@Service
public class UserItemService {

    private final NotificationService notificationService;

    private final UserItemRepository userItemRepository;

    private final ItemRepository itemRepository;

    public UserItemService(UserItemRepository userItemRepository, ItemRepository itemRepository,
            NotificationService notificationService) {
        this.userItemRepository = userItemRepository;
        this.itemRepository = itemRepository;
        this.notificationService = notificationService;
    }

    public List<UserItem> getInventoryForUser(User user) {
        if (user == null) {
            throw new DomainException("User is needed to retrieve all items.");
        }

        if (user.getHousehold() != null) {
            return userItemRepository.findByUser_Household_Id(user.getHousehold().getId());
        }

        return userItemRepository.findByUser_Username(user.getUsername());
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

        if (!results.isEmpty()) {
            // If batch is 1 item we link id to Notification
            Long relatedItemId = (results.size() == 1) ? results.get(0).id() : null;

            String message = (results.size() == 1)
                    ? "Item '" + results.get(0).itemName() + "' succesfully saved!"
                    : results.size() + " items succesfully saved!";

            sendSuccessNotification(user, message, relatedItemId);
        }

        return results;
    }

    private void sendSuccessNotification(User user, String message, Long relatedItemId) {
        // Use NotificationService for persistance and Firebase push notification
        notificationService.createAndSendNotification(
                user,
                "Inventory Update",
                message,
                relatedItemId);
    }
}

    @Transactional
    public void deleteUserItem(Long id, User user) {
        UserItem userItem = userItemRepository.findById(id)
            .orElseThrow(() -> new DomainException("Item not found with id: " + id));

        boolean isOwner = userItem.getUser().getId().equals(user.getId());
        boolean isSameHousehold = user.getHousehold() != null &&
                                  userItem.getUser().getHousehold() != null &&
                                  user.getHousehold().getId().equals(userItem.getUser().getHousehold().getId());

        if (!isOwner && !isSameHousehold) {
            throw new DomainException("You do not have permission to delete this item.");
        }
        
        userItemRepository.delete(userItem);

        sendSuccessNotification(user, "Instance of item successfully removed!", null);
    }
}
