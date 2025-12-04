package be.ucll.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import be.ucll.dto.ItemDTO;
import be.ucll.dto.UserItemDTO;
import be.ucll.dto.ItemDTO;
import be.ucll.dto.UserItemDTO;
import be.ucll.exception.DomainException;
import be.ucll.model.Item;
import be.ucll.repository.ItemRepository;
import jakarta.transaction.Transactional;

@Service
public class ItemService {
    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public Item getItemById(Long id) {
        return itemRepository.findById(id)
            .orElseThrow(() -> new DomainException("Item not found with id " + id));
    }

    public Item createItem(Item item) {
        if (item.getName() == null || item.getName().isBlank()) {
            throw new DomainException("Item name is required.");
        }
        if (item.getExpirationDate() == null) {
            throw new DomainException("Item expiration date is required.");
        }
        return itemRepository.save(item);
    }

    @Transactional
    public Item updateItem(Long id, Item updatedItem) {
        Item existingItem = getItemById(id);

        if (updatedItem.getName() != null) {
            existingItem.setName(updatedItem.getName());
        }

        if (updatedItem.getCategory() != null) {
            existingItem.setCategory(updatedItem.getCategory());
        }

        if (updatedItem.getQuantity() != null) {
            existingItem.setQuantity(updatedItem.getQuantity());
        }

        if (updatedItem.getExpirationDate() != null) {
            existingItem.setExpirationDate(updatedItem.getExpirationDate());
        }

        if (updatedItem.getOpenedDate() != null) {
            existingItem.setOpenedDate(updatedItem.getOpenedDate());
        }

        if (updatedItem.getDescription() != null) {
            existingItem.setDescription(updatedItem.getDescription());
        }

        return itemRepository.save(existingItem);
    }

    @Transactional
    public Item partiallyUpdateItem(Long id, Map<String, Object> updates) {
        Item existingItem = getItemById(id);

        updates.forEach((key, value) -> {
            switch (key) {
                case "name" -> existingItem.setName((String) value);
                case "category" -> {
                    try {
                        existingItem.setCategory(Item.Category.valueOf(value.toString().toUpperCase()));
                    } catch (IllegalArgumentException e) {
                        throw new DomainException("Invalid item category: " + value);
                    }
                }
                case "quantity" -> existingItem.setQuantity(((Number) value).intValue());
                case "expirationDate" -> existingItem.setExpirationDate(java.time.LocalDate.parse(value.toString()));
                case "openedDate" -> existingItem.setOpenedDate(java.time.LocalDate.parse(value.toString()));
                case "description" -> existingItem.setDescription((String) value);
                default -> throw new DomainException("Invalid field: " + key);
            }
        });

        return itemRepository.save(existingItem);
    }

    @Transactional
    public void deleteItem(Long id) {
        Item item = getItemById(id);
        itemRepository.delete(item);
    }

    public List<Item> searchItems(String name) {
        return itemRepository.findByNameContainingIgnoreCase(name);
    }

    private ItemDTO convertToDTO(Item item) {
        List<UserItemDTO> users = item.getUserItems().stream()
            .map(ui -> new UserItemDTO(
                ui.getUser().getId(),
                ui.getUser().getUsername(),
                ui.getExpirationDate(),
                ui.getOpenedDate(),
                ui.getOpenedRule()
            ))
            .toList();
        return new ItemDTO(
            item.getId(),
            item.getName(),
            item.getCategory().toString(),
            item.getQuantity(),
            item.getExpirationDate(),
            item.getDescription(),
            users
        );
    }
}
