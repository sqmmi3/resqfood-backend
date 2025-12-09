package be.ucll.service;

import java.util.List;

import org.springframework.stereotype.Service;

import be.ucll.exception.DomainException;
import be.ucll.model.Item;
import be.ucll.repository.ItemRepository;

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
            .orElseThrow(() -> new DomainException("Item not found"));
    }

    public Item createItem(Item item) {
        if (item.getName() == null || item.getName().isBlank()) {
            throw new DomainException("Item name is required");
        }
        return itemRepository.save(item);
    }

    public Item updateItem(Long id, Item item) {
        Item existing = getItemById(id);
        if (item.getName() != null) existing.setName(item.getName());
        return itemRepository.save(existing);
    }

    public void deleteItem(Long id) {
        itemRepository.delete(getItemById(id));
    }

    public Item searchItems(String name) {
        return itemRepository.findByNameContainingIgnoreCase(name);
    }
}
