package be.ucll.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import be.ucll.model.Item;
import be.ucll.repository.ItemRepository;

@Service
public class ItemService {
    final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public Item getItemById(Long id) {
        return itemRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Item not found with id " + id));
    }

    public Item createItem(Item item) {
        return itemRepository.save(item);
    }

    public Item updateItem(Long id, Item updatedItem) {
        Item existingItem = getItemById(id);

        if (updatedItem.getName() != null) {
            existingItem.setName(updatedItem.getName());
        }

        if (updatedItem.getExpirationDate() != null) {
            existingItem.setExpirationDate(updatedItem.getExpirationDate());
        }

        if (updatedItem.getOpenedDate() != null) {
            existingItem.setOpenedDate(updatedItem.getOpenedDate());
        }

        if (updatedItem.getOpenedRule() != null) {
            existingItem.setOpenedRule(updatedItem.getOpenedRule());
        }

        return itemRepository.save(existingItem);
    }

    public Item partiallyUpdateItem(Long id, Map<String, Object> updates) {
        Item existingItem = getItemById(id);

        updates.forEach((key, value) -> {
            switch (key) {
                case "name" -> existingItem.setName((String) value);
                case "expirationDate" -> existingItem.setExpirationDate((LocalDate) value);
                case "openedDate" -> existingItem.setOpenedDate((LocalDate) value);
                case "openedRule" -> existingItem.setOpenedRule((Integer) value);

                default -> throw new IllegalArgumentException("Invalid field: " + key);
            }
        });

        return itemRepository.save(existingItem);
    }

    public void deleteItem(Long id) {
        Item item = getItemById(id);
        itemRepository.delete(item);
    }

    public List<Item> searchItems(String name) {
        return itemRepository.findByNameContainingIgnoreCase(name);
    }
}
