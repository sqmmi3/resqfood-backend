package be.ucll.unit.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import be.ucll.dto.UserItemResponseDTO;
import be.ucll.mapper.UserItemMapper;
import be.ucll.model.Item;
import be.ucll.model.UserItem;

public class UserItemMapperTest {

    @Test
    void toDTO_happyPath() {
        // Given
        Item item = new Item("Banana", Item.Type.FRUIT);

        UserItem userItem = new UserItem();
        userItem.setId(100L);
        userItem.setItem(item);
        userItem.setExpirationDate(LocalDate.now().plusDays(5));
        userItem.setOpenedDate(LocalDate.now());
        userItem.setOpenedRule(3);
        userItem.setDescription("Organic Bananas");

        // When
        UserItemResponseDTO dto = UserItemMapper.toDTO(userItem);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(100L);
        assertThat(dto.itemName()).isEqualTo("Banana");
        assertThat(dto.type()).isEqualTo("FRUIT");
        assertThat(dto.expirationDate()).isEqualTo(LocalDate.now().plusDays(5));
        assertThat(dto.openedDate()).isEqualTo(LocalDate.now());
        assertThat(dto.openedRule()).isEqualTo(3);
        assertThat(dto.description()).isEqualTo("Organic Bananas");
    }

}
