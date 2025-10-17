package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CommentRepository commentRepository;

    @Test
    void findByItemIdOrderByCreatedDesc_ShouldReturnCommentsForItem() {
        // Given
        User owner = createUser("owner@mail.com", "Owner");
        User author1 = createUser("author1@mail.com", "Author One");
        User author2 = createUser("author2@mail.com", "Author Two");

        Item item1 = createItem("Item 1", "Description 1", true, owner);
        Item item2 = createItem("Item 2", "Description 2", true, owner);

        // Создаем комментарии для item1
        Comment comment1 = createComment("First comment", item1, author1,
                LocalDateTime.of(2024, 1, 1, 10, 0));
        Comment comment2 = createComment("Second comment", item1, author2,
                LocalDateTime.of(2024, 1, 2, 10, 0));

        // Комментарий для item2 (не должен попасть в результат)
        createComment("Other item comment", item2, author1,
                LocalDateTime.of(2024, 1, 3, 10, 0));

        // When
        List<Comment> result = commentRepository.findByItemIdOrderByCreatedDesc(item1.getId());

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCreated()).isAfter(result.get(1).getCreated()); // Проверяем сортировку
        assertThat(result.get(0).getText()).isEqualTo("Second comment");
        assertThat(result.get(1).getText()).isEqualTo("First comment");

        // Проверяем что JOIN FETCH работает - автор должен быть загружен
        assertThat(result.get(0).getAuthor()).isNotNull();
        assertThat(result.get(0).getAuthor().getName()).isEqualTo("Author Two");
    }

    @Test
    void findByItemIdInOrderByCreatedDesc_ShouldReturnCommentsForMultipleItems() {
        // Given
        User owner = createUser("owner@mail.com", "Owner");
        User author = createUser("author@mail.com", "Author");

        Item item1 = createItem("Item 1", "Description 1", true, owner);
        Item item2 = createItem("Item 2", "Description 2", true, owner);
        Item item3 = createItem("Item 3", "Description 3", true, owner);

        // Комментарии для разных вещей
        Comment comment1 = createComment("Comment for item1", item1, author,
                LocalDateTime.of(2024, 1, 1, 10, 0));
        Comment comment2 = createComment("Comment for item2", item2, author,
                LocalDateTime.of(2024, 1, 2, 10, 0));
        Comment comment3 = createComment("Another comment for item1", item1, author,
                LocalDateTime.of(2024, 1, 3, 10, 0));

        // When - ищем комментарии для item1 и item2
        List<Comment> result = commentRepository.findByItemIdInOrderByCreatedDesc(
                List.of(item1.getId(), item2.getId()));

        // Then
        assertThat(result).hasSize(3);

        // Проверяем что все комментарии относятся к запрошенным вещам
        assertThat(result).allMatch(comment ->
                comment.getItem().getId().equals(item1.getId()) ||
                        comment.getItem().getId().equals(item2.getId()));

        // Проверяем сортировку - сначала по itemId, потом по created DESC
        // Комментарии должны быть сгруппированы по вещам и отсортированы по дате внутри каждой группы
        assertThat(result.get(0).getItem().getId()).isEqualTo(item1.getId());
        assertThat(result.get(0).getText()).isEqualTo("Another comment for item1");
        assertThat(result.get(1).getItem().getId()).isEqualTo(item1.getId());
        assertThat(result.get(1).getText()).isEqualTo("Comment for item1");
        assertThat(result.get(2).getItem().getId()).isEqualTo(item2.getId());
    }

    @Test
    void existsByAuthorIdAndItemId_WhenCommentExists_ShouldReturnTrue() {
        // Given
        User owner = createUser("owner@mail.com", "Owner");
        User author = createUser("author@mail.com", "Author");
        Item item = createItem("Test Item", "Description", true, owner);

        createComment("Test comment", item, author, LocalDateTime.now());

        // When
        boolean exists = commentRepository.existsByAuthorIdAndItemId(author.getId(), item.getId());

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void existsByAuthorIdAndItemId_WhenCommentNotExists_ShouldReturnFalse() {
        // Given
        User owner = createUser("owner@mail.com", "Owner");
        User author = createUser("author@mail.com", "Author");
        User otherUser = createUser("other@mail.com", "Other User");
        Item item = createItem("Test Item", "Description", true, owner);
        Item otherItem = createItem("Other Item", "Description", true, owner);

        // Создаем комментарий от author к item
        createComment("Test comment", item, author, LocalDateTime.now());

        // When & Then - проверяем различные случаи когда комментария нет
        assertThat(commentRepository.existsByAuthorIdAndItemId(otherUser.getId(), item.getId())).isFalse();
        assertThat(commentRepository.existsByAuthorIdAndItemId(author.getId(), otherItem.getId())).isFalse();
        assertThat(commentRepository.existsByAuthorIdAndItemId(otherUser.getId(), otherItem.getId())).isFalse();
    }

    @Test
    void findByItemIdOrderByCreatedDesc_WhenNoComments_ShouldReturnEmptyList() {
        // Given
        User owner = createUser("owner@mail.com", "Owner");
        Item item = createItem("Test Item", "Description", true, owner);

        // When
        List<Comment> result = commentRepository.findByItemIdOrderByCreatedDesc(item.getId());

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void findByItemIdInOrderByCreatedDesc_WithEmptyList_ShouldReturnEmptyList() {
        // When
        List<Comment> result = commentRepository.findByItemIdInOrderByCreatedDesc(List.of());

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void findByItemIdInOrderByCreatedDesc_WithNonExistingItems_ShouldReturnEmptyList() {
        // Given
        User owner = createUser("owner@mail.com", "Owner");
        User author = createUser("author@mail.com", "Author");
        Item item = createItem("Test Item", "Description", true, owner);

        // Создаем комментарий для существующей вещи
        createComment("Test comment", item, author, LocalDateTime.now());

        // When - ищем комментарии для несуществующих вещей
        List<Comment> result = commentRepository.findByItemIdInOrderByCreatedDesc(List.of(999L, 1000L));

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void shouldEagerlyFetchAuthor() {
        // Given
        User owner = createUser("owner@mail.com", "Owner");
        User author = createUser("author@mail.com", "Author");
        Item item = createItem("Test Item", "Description", true, owner);

        createComment("Test comment", item, author, LocalDateTime.now());

        // When
        List<Comment> result = commentRepository.findByItemIdOrderByCreatedDesc(item.getId());

        // Then - проверяем что автор загружен eagerly благодаря JOIN FETCH
        assertThat(result).isNotEmpty();
        Comment comment = result.get(0);

        // Автор должен быть доступен без LazyInitializationException
        assertThat(comment.getAuthor()).isNotNull();
        assertThat(comment.getAuthor().getName()).isEqualTo("Author");
        assertThat(comment.getAuthor().getEmail()).isEqualTo("author@mail.com");
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

    private Comment createComment(String text, Item item, User author, LocalDateTime created) {
        Comment comment = Comment.builder()
                .text(text)
                .item(item)
                .author(author)
                .created(created)
                .build();
        return entityManager.persistAndFlush(comment);
    }
}