package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.item.dto.CommentCreateDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentCreateDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldSerializeCommentCreateDto() throws Exception {
        CommentCreateDto dto = new CommentCreateDto();
        dto.setText("Great item!");

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"text\":\"Great item!\"");
        assertThat(json).doesNotContain("\"id\""); // Убеждаемся, что нет лишних полей
        assertThat(json).doesNotContain("\"authorName\"");
        assertThat(json).doesNotContain("\"created\"");
    }

    @Test
    void shouldDeserializeCommentCreateDto() throws Exception {
        String json = "{\"text\":\"Excellent quality!\"}";

        CommentCreateDto dto = objectMapper.readValue(json, CommentCreateDto.class);

        assertThat(dto.getText()).isEqualTo("Excellent quality!");
    }

    @Test
    void shouldHandleEmptyText() throws Exception {
        CommentCreateDto dto = new CommentCreateDto();
        dto.setText("");

        String json = objectMapper.writeValueAsString(dto);
        CommentCreateDto result = objectMapper.readValue(json, CommentCreateDto.class);

        assertThat(result.getText()).isEmpty();
    }

    @Test
    void shouldHandleNullText() throws Exception {
        CommentCreateDto dto = new CommentCreateDto();
        // text остается null

        String json = objectMapper.writeValueAsString(dto);
        CommentCreateDto result = objectMapper.readValue(json, CommentCreateDto.class);

        assertThat(result.getText()).isNull();
    }

    @Test
    void shouldUseAllArgsConstructor() {
        CommentCreateDto dto = new CommentCreateDto("Test comment");

        assertThat(dto.getText()).isEqualTo("Test comment");
    }

    @Test
    void shouldUseNoArgsConstructor() {
        CommentCreateDto dto = new CommentCreateDto();

        assertThat(dto.getText()).isNull();

        dto.setText("New text");
        assertThat(dto.getText()).isEqualTo("New text");
    }
}