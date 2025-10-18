package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.BookingInfoDto;
import ru.practicum.shareit.item.dto.CommentDto;

import java.util.List;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldSerializeItemDto() throws Exception {
        ItemDto dto = new ItemDto();
        dto.setId(1L);
        dto.setName("Item");
        dto.setDescription("Description");
        dto.setAvailable(true);
        dto.setRequestId(2L);

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"name\":\"Item\"");
        assertThat(json).contains("\"description\":\"Description\"");
        assertThat(json).contains("\"available\":true");
        assertThat(json).contains("\"requestId\":2");
    }

    @Test
    void shouldDeserializeItemDto() throws Exception {
        String json = "{\"id\":1,\"name\":\"Item\",\"description\":\"Description\",\"available\":true,\"requestId\":2}";

        ItemDto dto = objectMapper.readValue(json, ItemDto.class);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Item");
        assertThat(dto.getDescription()).isEqualTo("Description");
        assertThat(dto.getAvailable()).isTrue();
        assertThat(dto.getRequestId()).isEqualTo(2L);
    }

    @Test
    void shouldSerializeItemDtoWithComplexFields() throws Exception {
        // Создаем BookingInfoDto для lastBooking и nextBooking
        BookingInfoDto lastBooking = new BookingInfoDto(1L, 2L,
                LocalDateTime.of(2024, 1, 1, 10, 0),
                LocalDateTime.of(2024, 1, 2, 10, 0));

        BookingInfoDto nextBooking = new BookingInfoDto(3L, 4L,
                LocalDateTime.of(2024, 1, 3, 10, 0),
                LocalDateTime.of(2024, 1, 4, 10, 0));

        // Создаем комментарии
        List<CommentDto> comments = List.of(
                new CommentDto(1L, "Great item!", "John", LocalDateTime.now()),
                new CommentDto(2L, "Excellent quality!", "Jane", LocalDateTime.now())
        );

        ItemDto dto = ItemDto.builder()
                .id(1L)
                .name("Item")
                .description("Description")
                .available(true)
                .requestId(5L)
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(comments)
                .build();

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"lastBooking\"");
        assertThat(json).contains("\"nextBooking\"");
        assertThat(json).contains("\"comments\"");
        assertThat(json).contains("\"Great item!\"");
    }

    @Test
    void shouldUseBuilderPattern() {
        ItemDto dto = ItemDto.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .requestId(2L)
                .build();

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Test Item");
        assertThat(dto.getDescription()).isEqualTo("Test Description");
        assertThat(dto.getAvailable()).isTrue();
        assertThat(dto.getRequestId()).isEqualTo(2L);
        assertThat(dto.getLastBooking()).isNull();
        assertThat(dto.getNextBooking()).isNull();
        assertThat(dto.getComments()).isNull();
    }

    @Test
    void shouldHandleNullComplexFields() throws Exception {
        ItemDto dto = new ItemDto(1L, "Item", "Description", true, 2L, null, null, null);

        String json = objectMapper.writeValueAsString(dto);
        ItemDto result = objectMapper.readValue(json, ItemDto.class);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getLastBooking()).isNull();
        assertThat(result.getNextBooking()).isNull();
        assertThat(result.getComments()).isNull();
    }
}