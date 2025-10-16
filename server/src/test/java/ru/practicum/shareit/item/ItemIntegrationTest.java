package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ItemIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Test
    void createAndGetItem_ShouldWorkSuccessfully() {
        // Given
        UserDto owner = createUser("owner@mail.com", "Owner");
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);

        // When
        ItemDto createdItem = itemService.create(itemDto, owner.getId());
        ItemDto retrievedItem = itemService.getById(createdItem.getId(), owner.getId());

        // Then
        assertNotNull(createdItem);
        assertNotNull(retrievedItem);
        assertEquals(createdItem.getId(), retrievedItem.getId());
        assertEquals("Test Item", retrievedItem.getName());
        assertEquals("Test Description", retrievedItem.getDescription());
        assertTrue(retrievedItem.getAvailable());
    }

    @Test
    void getByOwnerId_ShouldReturnUserItems() {
        // Given
        UserDto owner = createUser("owner2@mail.com", "Owner2");

        ItemDto item1 = createItem("Item1", "Description1", true, owner.getId());
        ItemDto item2 = createItem("Item2", "Description2", true, owner.getId());

        // When
        List<ItemDto> result = itemService.getByOwnerId(owner.getId());

        // Then
        assertNotNull(result);
        assertTrue(result.size() >= 2);
        assertTrue(result.stream().anyMatch(item -> item.getName().equals("Item1")));
        assertTrue(result.stream().anyMatch(item -> item.getName().equals("Item2")));
    }

    @Test
    void updateItem_ShouldUpdateSuccessfully() {
        // Given
        UserDto owner = createUser("owner3@mail.com", "Owner3");
        ItemDto originalItem = createItem("Original", "Original Desc", true, owner.getId());

        ItemDto updateDto = new ItemDto();
        updateDto.setName("Updated");
        updateDto.setDescription("Updated Desc");

        // When
        ItemDto updatedItem = itemService.update(originalItem.getId(), updateDto, owner.getId());

        // Then
        assertNotNull(updatedItem);
        assertEquals("Updated", updatedItem.getName());
        assertEquals("Updated Desc", updatedItem.getDescription());
        assertTrue(updatedItem.getAvailable()); // Should remain unchanged
    }

    @Test
    void searchItems_ShouldReturnAvailableItems() {
        // Given
        UserDto owner = createUser("owner4@mail.com", "Owner4");
        createItem("Power Drill", "Heavy duty drill", true, owner.getId());
        createItem("Hammer", "Steel hammer", true, owner.getId());
        createItem("Broken Drill", "Not working", false, owner.getId());

        // When
        List<ItemDto> result = itemService.search("drill");

        // Then
        assertEquals(1, result.size());
        assertEquals("Power Drill", result.get(0).getName());
    }

    @Test
    void search_WithBlankText_ShouldReturnEmptyList() {
        // When
        List<ItemDto> result = itemService.search("   ");

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getById_WhenItemNotFound_ShouldThrowException() {
        assertThrows(NotFoundException.class, () -> itemService.getById(999L, 1L));
    }

    @Test
    void update_WhenNotOwner_ShouldThrowException() {
        // Given
        UserDto owner = createUser("owner5@mail.com", "Owner5");
        UserDto otherUser = createUser("other@mail.com", "Other");
        ItemDto item = createItem("Item", "Desc", true, owner.getId());

        ItemDto updateDto = new ItemDto();
        updateDto.setName("Updated");

        // When & Then
        assertThrows(NotFoundException.class, () ->
                itemService.update(item.getId(), updateDto, otherUser.getId()));
    }

    private UserDto createUser(String email, String name) {
        UserDto userDto = new UserDto();
        userDto.setEmail(email);
        userDto.setName(name);
        return userService.createUser(userDto);
    }

    private ItemDto createItem(String name, String description, Boolean available, Long ownerId) {
        ItemDto itemDto = new ItemDto();
        itemDto.setName(name);
        itemDto.setDescription(description);
        itemDto.setAvailable(available);
        return itemService.create(itemDto, ownerId);
    }
}