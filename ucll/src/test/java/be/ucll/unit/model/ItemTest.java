package be.ucll.unit.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import be.ucll.model.Item;
import be.ucll.model.UserItem;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@ExtendWith(MockitoExtension.class)
class ItemTest {
    // Global given
    private final String validName = "Bananas";
    private final Item.Type validType = Item.Type.FRUIT;
    private final Integer defaultOpenedRule = 3;

    private Validator validator;

    @Mock
    private UserItem mockUserItem;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // Happy path tests

    @Test
    void createItem_ShouldInitializeWithDefaults() {
        // When
        Item item = new Item(validName, validType);

        // Then
        assertThat(item.getName()).isEqualTo(validName);
        assertThat(item.getType()).isEqualTo(validType);
        assertThat(item.getOpenedRule()).isEqualTo(defaultOpenedRule);
        assertThat(item.getUserItems()).isEmpty();
    }

    @Test
    void updateItem_ShouldUpdateItemWithNewValues() {
        // Given
        Item item = new Item(validName, validType);
        String newName = "Brocolli";
        Item.Type newType = Item.Type.VEGETABLE;
        Integer newOpenedRule = 5;

        // When
        item.setName(newName);
        item.setType(newType);
        item.setOpenedRule(newOpenedRule);

        // Then
        assertThat(item.getName()).isEqualTo(newName);
        assertThat(item.getType()).isEqualTo(newType);
        assertThat(item.getOpenedRule()).isEqualTo(newOpenedRule);
    }

    @Test
    void addUserItem_ShouldEstablishBidirectionalRelationship() {
        // Given
        Item item = new Item(validName, validType);

        // When
        item.addUserItem(mockUserItem);

        // Then
        assertThat(item.getUserItems()).hasSize(1);
        assertThat(item.getUserItems()).containsExactly(mockUserItem);
        verify(mockUserItem).setItem(item);
    }

    @Test
    void removeUserItem_happyPath() {
        // Given
        Item item = new Item(validName, validType);
        item.addUserItem(mockUserItem);

        // When
        item.removeUserItem(mockUserItem);

        // Then
        assertThat(item.getUserItems()).doesNotContain(mockUserItem);
        verify(mockUserItem).setItem(null);
    }

    @Test
    void toString_ShouldReturnCorrectFormat() {
        Item item = new Item(validName, validType);
        
        String expected = "Item{id=null, name=Bananas, type=FRUIT}";
        
        assertThat(item.toString()).hasToString(expected);
    }

    // Unhappy path tests

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    void createItem_unhappyPath_invalidName(String invalidName) {
        // When
        Item item = new Item(invalidName, validType);

        // Then
        var violations = validator.validate(item);
        assertThat(violations).isNotEmpty();
    }

    @Test
    void createItem_unhappyPath_nullName() {
        // When
        Item item = new Item(null, validType);

        // Then
        var violations = validator.validate(item);
        assertThat(violations).isNotEmpty();
    }

}
