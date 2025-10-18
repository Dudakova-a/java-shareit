package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ItemEntityTest {

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void shouldSaveAndRetrieveItem() {
        // Given
        User owner = createUser("owner@mail.com", "Owner Name");

        Item item = Item.builder()
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .owner(owner)
                .build();

        // When
        Item savedItem = entityManager.persistAndFlush(item);

        // Then
        assertThat(savedItem.getId()).isNotNull();
        assertThat(savedItem.getName()).isEqualTo("Test Item");
        assertThat(savedItem.getDescription()).isEqualTo("Test Description");
        assertThat(savedItem.getAvailable()).isTrue();
        assertThat(savedItem.getOwner().getId()).isEqualTo(owner.getId());
        assertThat(savedItem.getRequest()).isNull();
    }

    @Test
    void shouldSaveItemWithRequest() {
        // Given
        User owner = createUser("owner@mail.com", "Owner");
        User requester = createUser("requester@mail.com", "Requester");

        ItemRequest request = ItemRequest.builder()
                .description("Need a drill")
                .requester(requester)
                .created(LocalDateTime.now())
                .build();
        entityManager.persistAndFlush(request);

        Item item = Item.builder()
                .name("Power Drill")
                .description("Heavy duty drill")
                .available(true)
                .owner(owner)
                .request(request)
                .build();

        // When
        Item savedItem = entityManager.persistAndFlush(item);

        // Then
        assertThat(savedItem.getId()).isNotNull();
        assertThat(savedItem.getRequest()).isNotNull();
        assertThat(savedItem.getRequest().getId()).isEqualTo(request.getId());
        assertThat(savedItem.getRequestId()).isEqualTo(request.getId());
    }

    @Test
    void shouldUpdateItem() {
        // Given
        User owner = createUser("owner@mail.com", "Owner");

        Item item = Item.builder()
                .name("Old Name")
                .description("Old Description")
                .available(true)
                .owner(owner)
                .build();

        Item savedItem = entityManager.persistAndFlush(item);

        // When
        savedItem.setName("New Name");
        savedItem.setDescription("New Description");
        savedItem.setAvailable(false);
        entityManager.persistAndFlush(savedItem);

        // Then
        Item updatedItem = entityManager.find(Item.class, savedItem.getId());
        assertThat(updatedItem.getName()).isEqualTo("New Name");
        assertThat(updatedItem.getDescription()).isEqualTo("New Description");
        assertThat(updatedItem.getAvailable()).isFalse();
    }

    @Test
    void shouldHaveCorrectColumnMappings() {
        // Given
        User owner = createUser("owner@mail.com", "Owner");

        Item item = Item.builder()
                .name("Test Item")
                .description("A very long description that should be handled properly by the database column with length 1000")
                .available(true)
                .owner(owner)
                .build();

        // When
        Item savedItem = entityManager.persistAndFlush(item);

        // Then
        assertThat(savedItem.getName()).isEqualTo("Test Item");
        assertThat(savedItem.getDescription()).hasSizeLessThanOrEqualTo(1000);
        assertThat(savedItem.getAvailable()).isTrue();

        // Проверяем связи
        assertThat(savedItem.getOwner()).isNotNull();
        assertThat(savedItem.getOwner().getId()).isEqualTo(owner.getId());
    }

    @Test
    void getRequestId_WhenRequestExists_ShouldReturnRequestId() {
        // Given
        User owner = createUser("owner@mail.com", "Owner");
        User requester = createUser("requester@mail.com", "Requester");

        ItemRequest request = ItemRequest.builder()
                .description("Need item")
                .requester(requester)
                .created(LocalDateTime.now())
                .build();
        entityManager.persistAndFlush(request);

        Item item = Item.builder()
                .name("Item")
                .description("Description")
                .available(true)
                .owner(owner)
                .request(request)
                .build();

        // When
        Item savedItem = entityManager.persistAndFlush(item);

        // Then
        assertThat(savedItem.getRequestId()).isEqualTo(request.getId());
    }

    @Test
    void getRequestId_WhenRequestIsNull_ShouldReturnNull() {
        // Given
        User owner = createUser("owner@mail.com", "Owner");

        Item item = Item.builder()
                .name("Item")
                .description("Description")
                .available(true)
                .owner(owner)
                .request(null)
                .build();

        // When
        Item savedItem = entityManager.persistAndFlush(item);

        // Then
        assertThat(savedItem.getRequestId()).isNull();
    }

    @Test
    void shouldHandleLongDescription() {
        // Given
        User owner = createUser("owner@mail.com", "Owner");

        String longDescription = "This is a very long description that might exceed typical lengths. " +
                "It's important to test that the database column can handle longer descriptions " +
                "without issues. The description column is defined with length 1000, " +
                "so this text should be within the limit. ".repeat(5); // Создаем длинный текст

        // Обрезаем до 1000 символов если нужно
        String description = longDescription.length() > 1000 ?
                longDescription.substring(0, 1000) : longDescription;

        Item item = Item.builder()
                .name("Test Item")
                .description(description)
                .available(true)
                .owner(owner)
                .build();

        // When
        Item savedItem = entityManager.persistAndFlush(item);

        // Then
        assertThat(savedItem.getDescription()).isEqualTo(description);
        assertThat(savedItem.getDescription().length()).isLessThanOrEqualTo(1000);
    }

    @Test
    void shouldMaintainOwnerRelationship() {
        // Given
        User owner = createUser("owner@mail.com", "Owner");

        Item item = Item.builder()
                .name("Item")
                .description("Description")
                .available(true)
                .owner(owner)
                .build();

        Item savedItem = entityManager.persistAndFlush(item);

        // When - получаем item заново
        entityManager.clear();
        Item retrievedItem = entityManager.find(Item.class, savedItem.getId());

        // Then - проверяем, что связь с owner сохранилась
        assertThat(retrievedItem.getOwner()).isNotNull();
        assertThat(retrievedItem.getOwner().getId()).isEqualTo(owner.getId());
        assertThat(retrievedItem.getOwner().getName()).isEqualTo("Owner");
    }

    @Test
    void shouldHandleNullAvailable() {
        // Given
        User owner = createUser("owner@mail.com", "Owner");

        Item item = Item.builder()
                .name("Item")
                .description("Description")
                .available(null) // available может быть null
                .owner(owner)
                .build();

        // When
        Item savedItem = entityManager.persistAndFlush(item);

        // Then
        assertThat(savedItem.getAvailable()).isNull();
    }

    // Вспомогательные методы
    private User createUser(String email, String name) {
        User user = User.builder()
                .email(email)
                .name(name)
                .build();
        return entityManager.persistAndFlush(user);
    }
}