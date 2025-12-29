package be.ucll.integration.controller;

import be.ucll.model.Item;
import be.ucll.repository.ItemRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "jwt.secret=Y8r3mP9wQ2tF6sV1xB7eH4kN0uJ5cR8Z",
        "jwt.expiration=3600000"
})
class ItemRestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        itemRepository.deleteAll();
    }

    // GET REQUESTS

    @Test
    void getAllItems_happyPath() throws Exception {
        // Givem
        itemRepository.save(new Item("Apple", Item.Type.FRUIT));
        itemRepository.save(new Item("Bread", Item.Type.GRAIN));

        // When
        mockMvc.perform(get("/items"))
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Apple")))
                .andExpect(jsonPath("$[1].name", is("Bread")));
    }

    @Test
    void getItemById_happyPath() throws Exception {
        // Given
        Item savedItem = itemRepository.save(new Item("Banana", Item.Type.FRUIT));

        // When
        mockMvc.perform(get("/items/" + savedItem.getId()))
                // THen
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Banana")));
    }

    @Test
    void getItemById_notFound() throws Exception {
        // When
        mockMvc.perform(get("/items/999"))
                // Then
                .andExpect(status().isInternalServerError());
    }

    // Post requests

    @Test
    void createItem_happyPath() throws Exception {
        // Given
        Item newItem = new Item("Steak", Item.Type.PROTEIN);

        // When
        mockMvc.perform(post("/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newItem)))
                // THen
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name", is("Steak")));
    }

    @Test
    void createItem_invalid() throws Exception {
        // Given
        Item newItem = new Item(null, Item.Type.FRUIT);

        // When
        mockMvc.perform(post("/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newItem)))
                // Then
                .andExpect(status().isInternalServerError());
    }

    // PUT REQUESTS

    @Test
    void updateItem_happyPath() throws Exception {
        // Given
        Item savedItem = itemRepository.save(new Item("Oldname", Item.Type.DAIRY));
        Item updateData = new Item("NewName", Item.Type.DAIRY);

        // When
        mockMvc.perform(put("/items/" + savedItem.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData)))
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("NewName")));
    }

    // DELETE REQUESTS

    @Test
    void deleteItem_happyPath() throws Exception {
        // Given
        Item savedItem = itemRepository.save(new Item("Trash", Item.Type.SWEETS));

        // When
        mockMvc.perform(delete("/items/" + savedItem.getId()))
                // Then
                .andExpect(status().isNoContent());

        // Check item is gone
        mockMvc.perform(get("/items/" + savedItem.getId()))
                .andExpect(status().isInternalServerError());
    }

    // SEARCH REQUESTS

    @Test
    void searchItems_happyPath() throws Exception {
        // Given
        itemRepository.save(new Item("Frozen Apple", Item.Type.FROZEN));

        // When
        mockMvc.perform(get("/items/search")
                .param("name", "apple"))

                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Frozen Apple")));
    }

}
