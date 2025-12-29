package be.ucll.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import be.ucll.exception.DomainException;
import be.ucll.model.Item;
import be.ucll.repository.ItemRepository;
import be.ucll.service.ItemService;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    // Global given
    private final Long validId = 1L;
    private final String validName = "Apple";
    private final Item.Type validType = Item.Type.FRUIT;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemService itemService;

    @Test
    void getAllItems_happyPath() {
        // Given
        Item item1 = new Item("Apple", Item.Type.FRUIT);
        Item item2 = new Item("Bread", Item.Type.GRAIN);

        when(itemRepository.findAll()).thenReturn(List.of(item1, item2));

        // When
        List<Item> result = itemService.getAllItems();

        // Then
        assertThat(result).hasSize(2).containsExactly(item1, item2);
        verify(itemRepository).findAll();
    }

    @Test
    void getItemById_happyPath() {
        // Given
        Item item = new Item(validName, validType);
        when(itemRepository.findById(validId)).thenReturn(Optional.of(item));

        // When
        Item result = itemService.getItemById(validId);

        // Then
        assertThat(result).isEqualTo(item);
        verify(itemRepository).findById(validId);
    }

    @Test
    void getItemById_notFound_unhappyPath() {
        // Given
        when(itemRepository.findById(validId)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> itemService.getItemById(validId)).isInstanceOf(DomainException.class)
                .hasMessage("Item not found");
    }

    @Test
    void createItem_happyPath() {
        // Given
        Item item = new Item(validName, validType);
        when(itemRepository.save(item)).thenReturn(item);

        // When
        Item result = itemService.createItem(item);

        // Then
        assertThat(result).isEqualTo(item);
        verify(itemRepository).save(item);
    }

    @Test
    void createItem_unhappyPath_nullName() {
        // Given
        Item item = new Item(null, validType);

        // When / Then
        assertThatThrownBy(() -> itemService.createItem(item)).isInstanceOf(DomainException.class)
                .hasMessage("Item name is required");
    }

    @Test
    void createItem_unhappyPath_blankName() {
        // Given
        Item item = new Item("", validType);

        // When / Then
        assertThatThrownBy(() -> itemService.createItem(item)).isInstanceOf(DomainException.class)
                .hasMessage("Item name is required");
    }

    @Test
    void updateItem_happyPath() {
        // Given
        Item existingItem = new Item("Old Name", validType);
        Item updates = new Item("New Name", null); // Only update name

        when(itemRepository.findById(validId)).thenReturn(Optional.of(existingItem));
        when(itemRepository.save(existingItem)).thenReturn(existingItem);

        // When
        Item result = itemService.updateItem(validId, updates);

        // Then
        assertThat(result.getName()).isEqualTo("New Name");
        verify(itemRepository).save(existingItem);
    }

    @Test
    void updateItem_notFound_unhappyPath() {
        // Given
        Item updates = new Item("NewName", null);
        when(itemRepository.findById(validId)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> itemService.updateItem(validId, updates)).isInstanceOf(DomainException.class)
                .hasMessage("Item not found");
    }

    @Test
    void deleteItem_happyPath() {
        // Given
        Item existingItem = new Item(validName, validType);
        when(itemRepository.findById(validId)).thenReturn(Optional.of(existingItem));

        // When
        itemService.deleteItem(validId);

        // Then
        verify(itemRepository).delete(existingItem);
    }

    @Test
    void deleteItem_notFound_unhappyPath() {
        // Given
        when(itemRepository.findById(validId)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> itemService.deleteItem(validId)).isInstanceOf(DomainException.class)
                .hasMessage("Item not found");
    }

    @Test
    void searchItems_happyPath() {
        // Given
        String query = "app";
        Item match = new Item("Apple", validType);
        when(itemRepository.findByNameContainingIgnoreCase(query)).thenReturn(Optional.of(match));

        // When
        Item result = itemService.searchItem(query);

        // Then
        assertThat(result).isEqualTo(match);
    }
}
