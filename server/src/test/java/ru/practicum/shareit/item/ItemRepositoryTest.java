package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void findByOwnerId_ShouldReturnUserItems() {
        // Given
        User owner1 = createUser("owner1@mail.com", "Owner One");
        User owner2 = createUser("owner2@mail.com", "Owner Two");

        Item item1 = createItem("Item 1", "Description 1", true, owner1);
        Item item2 = createItem("Item 2", "Description 2", true, owner1);
        Item item3 = createItem("Item 3", "Description 3", true, owner2);

        // When
        List<Item> owner1Items = itemRepository.findByOwnerId(owner1.getId());
        List<Item> owner2Items = itemRepository.findByOwnerId(owner2.getId());

        // Then
        assertThat(owner1Items).hasSize(2);
        assertThat(owner1Items).allMatch(item -> item.getOwner().getId().equals(owner1.getId()));

        assertThat(owner2Items).hasSize(1);
        assertThat(owner2Items.get(0).getOwner().getId()).isEqualTo(owner2.getId());
    }

    @Test
    void findByOwnerId_WhenNoItems_ShouldReturnEmptyList() {
        // Given
        User owner = createUser("owner@mail.com", "Owner");

        // When
        List<Item> result = itemRepository.findByOwnerId(owner.getId());

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void searchAvailableItems_ShouldReturnAvailableItemsMatchingText() {
        // Given
        User owner = createUser("owner@mail.com", "Owner");

        // Доступные вещи, соответствующие поиску
        Item item1 = createItem("Power Drill", "Heavy duty drill", true, owner);
        Item item2 = createItem("Electric Drill", "Cordless drill", true, owner);

        // Недоступная вещь (не должна быть в результате)
        Item item3 = createItem("Broken Drill", "Not working", false, owner);

        // Доступная вещь, не соответствующая поиску (не должна быть в результате)
        Item item4 = createItem("Hammer", "Steel hammer", true, owner);

        // When
        List<Item> result = itemRepository.searchAvailableItems("drill");

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(Item::getAvailable);
        assertThat(result).allMatch(item ->
                item.getName().toLowerCase().contains("drill") ||
                        item.getDescription().toLowerCase().contains("drill"));
    }

    @Test
    void searchAvailableItems_WithCaseInsensitiveSearch_ShouldReturnItems() {
        // Given
        User owner = createUser("owner@mail.com", "Owner");
        createItem("POWER DRILL", "HEAVY DUTY DRILL", true, owner);
        createItem("power drill", "heavy duty drill", true, owner);
        createItem("Power Drill", "Heavy Duty Drill", true, owner);

        // When
        List<Item> result1 = itemRepository.searchAvailableItems("POWER");
        List<Item> result2 = itemRepository.searchAvailableItems("power");
        List<Item> result3 = itemRepository.searchAvailableItems("Power");

        // Then
        assertThat(result1).hasSize(3);
        assertThat(result2).hasSize(3);
        assertThat(result3).hasSize(3);
    }

    @Test
    void searchAvailableItems_WithEmptyText_ShouldReturnAllAvailableItems() {
        // Given
        User owner = createUser("owner@mail.com", "Owner");
        createItem("Drill", "Description", true, owner);
        createItem("Hammer", "Tool", true, owner);
        createItem("Broken Saw", "Not working", false, owner); // Недоступная - не должна быть в результате

        // When
        List<Item> result = itemRepository.searchAvailableItems("");

        // Then - при пустом тексте возвращаются ВСЕ доступные вещи
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(Item::getAvailable);
    }

    @Test
    void searchAvailableItems_WithNullText_ShouldReturnEmptyList() {
        // Given
        User owner = createUser("owner@mail.com", "Owner");
        createItem("Drill", "Description", true, owner);

        // When
        List<Item> result = itemRepository.searchAvailableItems(null);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void searchAvailableItems_WithPartialMatch_ShouldReturnItems() {
        // Given
        User owner = createUser("owner@mail.com", "Owner");
        createItem("Power Drill", "Heavy duty tool", true, owner);
        createItem("Hammer", "Tool for construction", true, owner);
        createItem("Screwdriver", "Tool for screws", true, owner);

        // When
        List<Item> result = itemRepository.searchAvailableItems("tool");

        // Then
        assertThat(result).hasSize(3);
        assertThat(result).allMatch(item ->
                item.getName().toLowerCase().contains("tool") ||
                        item.getDescription().toLowerCase().contains("tool"));
    }

    @Test
    void findByRequestId_ShouldReturnItemsForRequest() {
        // Given
        User owner = createUser("owner@mail.com", "Owner");
        User requester = createUser("requester@mail.com", "Requester");

        ItemRequest request1 = createItemRequest("Need tools", requester);
        ItemRequest request2 = createItemRequest("Need equipment", requester);

        Item item1 = createItemWithRequest("Drill", "Description", true, owner, request1);
        Item item2 = createItemWithRequest("Hammer", "Description", true, owner, request1);
        Item item3 = createItemWithRequest("Saw", "Description", true, owner, request2);

        // When
        List<Item> request1Items = itemRepository.findByRequestId(request1.getId());
        List<Item> request2Items = itemRepository.findByRequestId(request2.getId());

        // Then
        assertThat(request1Items).hasSize(2);
        assertThat(request1Items).allMatch(item -> item.getRequest().getId().equals(request1.getId()));

        assertThat(request2Items).hasSize(1);
        assertThat(request2Items.get(0).getRequest().getId()).isEqualTo(request2.getId());
    }

    @Test
    void findByRequestId_WhenNoItems_ShouldReturnEmptyList() {
        // Given
        User requester = createUser("requester@mail.com", "Requester");
        ItemRequest request = createItemRequest("Need item", requester);

        // When
        List<Item> result = itemRepository.findByRequestId(request.getId());

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void findByRequestId_WithNonExistingRequestId_ShouldReturnEmptyList() {
        // When
        List<Item> result = itemRepository.findByRequestId(999L);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void findByRequestId_WithNullRequestId_ShouldReturnEmptyList() {
        // Given
        User owner = createUser("owner@mail.com", "Owner");
        createItem("Item", "Description", true, owner); // item без request

        // When
        List<Item> result = itemRepository.findByRequestId(null);

        // Then
        assertThat(result).isEmpty();
    }

    // Вспомогательные методы
    private User createUser(String email, String name) {
        User user = User.builder()
                .email(email)
                .name(name)
                .build();
        return entityManager.persistAndFlush(user);
    }

    private Item createItem(String name, String description, Boolean available, User owner) {
        Item item = Item.builder()
                .name(name)
                .description(description)
                .available(available)
                .owner(owner)
                .build();
        return entityManager.persistAndFlush(item);
    }

    private Item createItemWithRequest(String name, String description, Boolean available, User owner, ItemRequest request) {
        Item item = Item.builder()
                .name(name)
                .description(description)
                .available(available)
                .owner(owner)
                .request(request)
                .build();
        return entityManager.persistAndFlush(item);
    }

    private ItemRequest createItemRequest(String description, User requester) {
        ItemRequest request = ItemRequest.builder()
                .description(description)
                .requester(requester)
                .created(LocalDateTime.now())
                .build();
        return entityManager.persistAndFlush(request);
    }
}