package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.item.dto.CommentDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldSerializeCommentDto() throws Exception {
        CommentDto dto = new CommentDto();
        dto.setId(1L);
        dto.setText("Great item!");
        dto.setAuthorName("John Doe");
        LocalDateTime created = LocalDateTime.of(2024, 1, 1, 10, 0);
        dto.setCreated(created);

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"text\":\"Great item!\"");
        assertThat(json).contains("\"authorName\":\"John Doe\"");
        assertThat(json).contains("\"created\":\"2024-01-01T10:00:00\"");
    }

    @Test
    void shouldDeserializeCommentDto() throws Exception {
        String json = "{" +
                "\"id\":1," +
                "\"text\":\"Excellent quality!\"," +
                "\"authorName\":\"Jane Smith\"," +
                "\"created\":\"2024-01-01T10:00:00\"" +
                "}";

        CommentDto dto = objectMapper.readValue(json, CommentDto.class);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getText()).isEqualTo("Excellent quality!");
        assertThat(dto.getAuthorName()).isEqualTo("Jane Smith");
        assertThat(dto.getCreated()).isEqualTo(LocalDateTime.of(2024, 1, 1, 10, 0));
    }

    @Test
    void shouldHandlePartialData() throws Exception {
        // Тест с неполными данными (например, без id)
        String json = "{" +
                "\"text\":\"Good item\"," +
                "\"authorName\":\"Anonymous\"" +
                "}";

        CommentDto dto = objectMapper.readValue(json, CommentDto.class);

        assertThat(dto.getId()).isNull();
        assertThat(dto.getText()).isEqualTo("Good item");
        assertThat(dto.getAuthorName()).isEqualTo("Anonymous");
        assertThat(dto.getCreated()).isNull();
    }

    @Test
    void shouldUseAllArgsConstructor() {
        LocalDateTime created = LocalDateTime.now();
        CommentDto dto = new CommentDto(1L, "Test comment", "Author Name", created);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getText()).isEqualTo("Test comment");
        assertThat(dto.getAuthorName()).isEqualTo("Author Name");
        assertThat(dto.getCreated()).isEqualTo(created);
    }

    @Test
    void shouldUseNoArgsConstructor() {
        CommentDto dto = new CommentDto();

        assertThat(dto.getId()).isNull();
        assertThat(dto.getText()).isNull();
        assertThat(dto.getAuthorName()).isNull();
        assertThat(dto.getCreated()).isNull();

        // Проверяем setters
        dto.setId(1L);
        dto.setText("New text");
        dto.setAuthorName("New author");
        LocalDateTime now = LocalDateTime.now();
        dto.setCreated(now);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getText()).isEqualTo("New text");
        assertThat(dto.getAuthorName()).isEqualTo("New author");
        assertThat(dto.getCreated()).isEqualTo(now);
    }

    @Test
    void shouldHandleLocalDateTimeFormat() throws Exception {
        // Проверяем различные форматы дат
        String json = "{" +
                "\"id\":1," +
                "\"text\":\"Test\"," +
                "\"authorName\":\"Test Author\"," +
                "\"created\":\"2024-12-31T23:59:59\"" +
                "}";

        CommentDto dto = objectMapper.readValue(json, CommentDto.class);

        assertThat(dto.getCreated()).isEqualTo(LocalDateTime.of(2024, 12, 31, 23, 59, 59));
    }
}