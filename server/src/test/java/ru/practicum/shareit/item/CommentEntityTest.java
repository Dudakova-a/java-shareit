package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CommentEntityTest {

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void shouldSaveAndRetrieveComment() {
        // Given - создаем пользователя и вещь
        User author = createUser("author@mail.com", "Author Name");
        User owner = createUser("owner@mail.com", "Owner Name");
        Item item = createItem("Test Item", "Test Description", true, owner);

        // When - создаем комментарий
        LocalDateTime created = LocalDateTime.now();
        Comment comment = Comment.builder()
                .text("Great item! Very useful.")
                .item(item)
                .author(author)
                .created(created)
                .build();

        Comment savedComment = entityManager.persistAndFlush(comment);

        // Then - проверяем сохранение
        assertThat(savedComment.getId()).isNotNull();
        assertThat(savedComment.getText()).isEqualTo("Great item! Very useful.");
        assertThat(savedComment.getItem().getId()).isEqualTo(item.getId());
        assertThat(savedComment.getAuthor().getId()).isEqualTo(author.getId());
        assertThat(savedComment.getCreated()).isEqualTo(created);
    }

    @Test
    void shouldUpdateCommentText() {
        // Given - сохраняем комментарий
        User author = createUser("author@mail.com", "Author");
        User owner = createUser("owner@mail.com", "Owner");
        Item item = createItem("Item", "Description", true, owner);

        Comment comment = Comment.builder()
                .text("Original comment")
                .item(item)
                .author(author)
                .created(LocalDateTime.now())
                .build();

        Comment savedComment = entityManager.persistAndFlush(comment);

        // When - обновляем текст
        savedComment.setText("Updated comment text");
        entityManager.persistAndFlush(savedComment);

        // Then - проверяем обновление
        Comment updatedComment = entityManager.find(Comment.class, savedComment.getId());
        assertThat(updatedComment.getText()).isEqualTo("Updated comment text");
    }

    @Test
    void shouldHaveCorrectColumnMappings() {
        // Given
        User author = createUser("author@mail.com", "Author");
        User owner = createUser("owner@mail.com", "Owner");
        Item item = createItem("Item", "Description", true, owner);

        LocalDateTime created = LocalDateTime.of(2024, 1, 1, 10, 0, 0);

        Comment comment = Comment.builder()
                .text("Test comment with maximum length considerations")
                .item(item)
                .author(author)
                .created(created)
                .build();

        // When
        Comment savedComment = entityManager.persistAndFlush(comment);

        // Then - проверяем маппинг полей
        assertThat(savedComment.getText()).isEqualTo("Test comment with maximum length considerations");
        assertThat(savedComment.getCreated()).isEqualTo(created);

        // Проверяем связи
        assertThat(savedComment.getItem()).isNotNull();
        assertThat(savedComment.getAuthor()).isNotNull();
    }

    @Test
    void shouldMaintainRelationships() {
        // Given
        User author = createUser("author@mail.com", "Author");
        User owner = createUser("owner@mail.com", "Owner");
        Item item = createItem("Item", "Description", true, owner);

        Comment comment = Comment.builder()
                .text("Relationship test comment")
                .item(item)
                .author(author)
                .created(LocalDateTime.now())
                .build();

        Comment savedComment = entityManager.persistAndFlush(comment);

        // When - получаем комментарий заново
        entityManager.clear();
        Comment retrievedComment = entityManager.find(Comment.class, savedComment.getId());

        // Then - проверяем, что связи сохранились
        assertThat(retrievedComment.getItem().getId()).isEqualTo(item.getId());
        assertThat(retrievedComment.getAuthor().getId()).isEqualTo(author.getId());
        assertThat(retrievedComment.getItem().getOwner().getId()).isEqualTo(owner.getId());
    }

    @Test
    void shouldHandleLongText() {
        // Given - проверяем обработку длинного текста
        User author = createUser("author@mail.com", "Author");
        User owner = createUser("owner@mail.com", "Owner");
        Item item = createItem("Item", "Description", true, owner);

        String longText = "This is a very long comment text that might exceed typical lengths. " +
                "It's important to test that the database column can handle longer comments " +
                "without issues. The text column is defined with length 1000, so this should be fine.";

        Comment comment = Comment.builder()
                .text(longText)
                .item(item)
                .author(author)
                .created(LocalDateTime.now())
                .build();

        // When
        Comment savedComment = entityManager.persistAndFlush(comment);

        // Then
        assertThat(savedComment.getText()).isEqualTo(longText);
        assertThat(savedComment.getText().length()).isLessThanOrEqualTo(1000); // Проверяем ограничение длины
    }

    @Test
    void shouldCascadeOperationsCorrectly() {
        // Given
        User author = createUser("author@mail.com", "Author");
        User owner = createUser("owner@mail.com", "Owner");
        Item item = createItem("Item", "Description", true, owner);

        Comment comment1 = Comment.builder()
                .text("First comment")
                .item(item)
                .author(author)
                .created(LocalDateTime.now())
                .build();

        Comment comment2 = Comment.builder()
                .text("Second comment")
                .item(item)
                .author(author)
                .created(LocalDateTime.now().plusHours(1))
                .build();

        // When - сохраняем несколько комментариев для одной вещи
        entityManager.persistAndFlush(comment1);
        entityManager.persistAndFlush(comment2);

        // Then - проверяем, что оба комментария сохранились
        assertThat(comment1.getId()).isNotNull();
        assertThat(comment2.getId()).isNotNull();
        assertThat(comment1.getItem().getId()).isEqualTo(comment2.getItem().getId());
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
}