package be.ucll.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

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
            throw new DomainException("Item name is reqguired.");
        }
        if (item.getType() == null) {
            throw new DomainException("Item type is required.");
        }
        return itemRepository.save(item);
    }

    @Transactional
    public Item updateItem(Long id, Item updatedItem) {
        Item existingItem = getItemById(id);

        if (updatedItem.getName() != null) {
            existingItem.setName(updatedItem.getName());
        }

        if (updatedItem.getType() != null) {
            existingItem.setType(updatedItem.getType());
        }

        return itemRepository.save(existingItem);
    }

    @Transactional
    public Item partiallyUpdateItem(Long id, Map<String, Object> updates) {
        Item existingItem = getItemById(id);

        updates.forEach((key, value) -> {
            switch (key) {
                case "name" -> existingItem.setName((String) value);
                case "type" -> {
                    try {
                        existingItem.setType(Item.Type.valueOf(value.toString().toUpperCase()));
                    } catch (IllegalArgumentException e) {
                        throw new DomainException("Invalid item type: " + value);
                    }
                }
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
}
