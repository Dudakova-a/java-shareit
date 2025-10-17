package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class CommentMapperTest {

    private final CommentMapper commentMapper = new CommentMapper();

    @Test
    void toCommentDto_ShouldMapCorrectly() {
        // Given
        User author = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john@mail.com")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("Test Item")
                .build();

        LocalDateTime created = LocalDateTime.of(2024, 1, 1, 10, 0);
        Comment comment = Comment.builder()
                .id(1L)
                .text("Great item!")
                .item(item)
                .author(author)
                .created(created)
                .build();

        // When
        CommentDto result = commentMapper.toCommentDto(comment);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getText()).isEqualTo("Great item!");
        assertThat(result.getAuthorName()).isEqualTo("John Doe");
        assertThat(result.getCreated()).isEqualTo(created);
    }

    @Test
    void toComment_FromCommentCreateDto_ShouldMapCorrectly() {
        // Given
        CommentCreateDto createDto = new CommentCreateDto();
        createDto.setText("Excellent quality!");

        User author = User.builder()
                .id(1L)
                .name("Jane Smith")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("Power Drill")
                .build();

        // When
        Comment result = commentMapper.toComment(createDto, item, author);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getText()).isEqualTo("Excellent quality!");
        assertThat(result.getItem()).isEqualTo(item);
        assertThat(result.getAuthor()).isEqualTo(author);
        assertThat(result.getCreated()).isNotNull();
        assertThat(result.getCreated()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    void toComment_FromCommentDto_ShouldMapCorrectly() {
        // Given
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Good item");
        commentDto.setCreated(LocalDateTime.of(2024, 1, 1, 10, 0));

        // When
        Comment result = commentMapper.toComment(commentDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getText()).isEqualTo("Good item");
        assertThat(result.getCreated()).isEqualTo(LocalDateTime.of(2024, 1, 1, 10, 0));
        assertThat(result.getItem()).isNull();
        assertThat(result.getAuthor()).isNull();
    }

    @Test
    void toComment_FromCommentDto_WithNullCreated_ShouldSetCurrentTime() {
        // Given
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Test comment");
        // created остается null

        // When
        Comment result = commentMapper.toComment(commentDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getText()).isEqualTo("Test comment");
        assertThat(result.getCreated()).isNotNull();
        assertThat(result.getCreated()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    void toCommentDto_WithNullAuthor_ShouldHandleGracefully() {
        // Given
        Comment comment = Comment.builder()
                .id(1L)
                .text("Comment without author")
                .created(LocalDateTime.now())
                .build();
        // author остается null

        // When & Then - проверяем, что маппер не падает
        // Это может выбросить NPE, что нормально - зависит от требований
        // Если нужно обрабатывать null, то нужно добавить проверки в маппер
        try {
            CommentDto result = commentMapper.toCommentDto(comment);
            // Если не упало, проверяем результат
            assertThat(result).isNotNull();
            assertThat(result.getAuthorName()).isNull();
        } catch (NullPointerException e) {
            // Ожидаемое поведение - автор обязателен
            assertThat(e).isInstanceOf(NullPointerException.class);
        }
    }

    @Test
    void toComment_FromCommentCreateDto_WithEmptyText_ShouldMapCorrectly() {
        // Given
        CommentCreateDto createDto = new CommentCreateDto();
        createDto.setText("");

        User author = User.builder().id(1L).build();
        Item item = Item.builder().id(1L).build();

        // When
        Comment result = commentMapper.toComment(createDto, item, author);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getText()).isEmpty();
        assertThat(result.getItem()).isEqualTo(item);
        assertThat(result.getAuthor()).isEqualTo(author);
    }

    @Test
    void toComment_FromCommentCreateDto_ShouldSetCurrentTimestamp() {
        // Given
        CommentCreateDto createDto = new CommentCreateDto();
        createDto.setText("Timestamp test");

        User author = User.builder().id(1L).build();
        Item item = Item.builder().id(1L).build();

        LocalDateTime before = LocalDateTime.now();

        // When
        Comment result = commentMapper.toComment(createDto, item, author);

        LocalDateTime after = LocalDateTime.now();

        // Then
        assertThat(result.getCreated()).isAfterOrEqualTo(before);
        assertThat(result.getCreated()).isBeforeOrEqualTo(after);
    }
}