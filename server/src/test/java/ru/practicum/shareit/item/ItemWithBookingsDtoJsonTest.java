package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.dto.BookingInfoDto;
import ru.practicum.shareit.item.dto.CommentDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemWithBookingsDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldSerializeItemWithBookingsDto() throws Exception {
        // Создаем BookingInfoDto для бронирований
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

        ItemWithBookingsDto dto = new ItemWithBookingsDto(
                1L, "Power Drill", "Heavy duty drill", true, 5L,
                lastBooking, nextBooking, comments
        );

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"name\":\"Power Drill\"");
        assertThat(json).contains("\"description\":\"Heavy duty drill\"");
        assertThat(json).contains("\"available\":true");
        assertThat(json).contains("\"requestId\":5");
        assertThat(json).contains("\"lastBooking\"");
        assertThat(json).contains("\"nextBooking\"");
        assertThat(json).contains("\"comments\"");
        assertThat(json).contains("\"Great item!\"");
    }

    @Test
    void shouldDeserializeItemWithBookingsDto() throws Exception {
        String json = "{" +
                "\"id\":1," +
                "\"name\":\"Test Item\"," +
                "\"description\":\"Test Description\"," +
                "\"available\":true," +
                "\"requestId\":2," +
                "\"lastBooking\":{" +
                "   \"id\":1," +
                "   \"bookerId\":2," +
                "   \"start\":\"2024-01-01T10:00:00\"," +
                "   \"end\":\"2024-01-02T10:00:00\"" +
                "}," +
                "\"nextBooking\":{" +
                "   \"id\":3," +
                "   \"bookerId\":4," +
                "   \"start\":\"2024-01-03T10:00:00\"," +
                "   \"end\":\"2024-01-04T10:00:00\"" +
                "}," +
                "\"comments\":[" +
                "   {" +
                "       \"id\":1," +
                "       \"text\":\"Great item!\"," +
                "       \"authorName\":\"John\"," +
                "       \"created\":\"2024-01-01T10:00:00\"" +
                "   }" +
                "]" +
                "}";

        ItemWithBookingsDto dto = objectMapper.readValue(json, ItemWithBookingsDto.class);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Test Item");
        assertThat(dto.getDescription()).isEqualTo("Test Description");
        assertThat(dto.getAvailable()).isTrue();
        assertThat(dto.getRequestId()).isEqualTo(2L);

        // Проверяем lastBooking
        assertThat(dto.getLastBooking()).isNotNull();
        assertThat(dto.getLastBooking().getId()).isEqualTo(1L);
        assertThat(dto.getLastBooking().getBookerId()).isEqualTo(2L);

        // Проверяем nextBooking
        assertThat(dto.getNextBooking()).isNotNull();
        assertThat(dto.getNextBooking().getId()).isEqualTo(3L);
        assertThat(dto.getNextBooking().getBookerId()).isEqualTo(4L);

        // Проверяем comments
        assertThat(dto.getComments()).hasSize(1);
        assertThat(dto.getComments().get(0).getText()).isEqualTo("Great item!");
    }

    @Test
    void shouldHandleNullComplexFields() throws Exception {
        ItemWithBookingsDto dto = new ItemWithBookingsDto(
                1L, "Item", "Description", true, 2L,
                null, null, null
        );

        String json = objectMapper.writeValueAsString(dto);
        ItemWithBookingsDto result = objectMapper.readValue(json, ItemWithBookingsDto.class);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getLastBooking()).isNull();
        assertThat(result.getNextBooking()).isNull();
        assertThat(result.getComments()).isNull();
    }

    @Test
    void shouldHandleEmptyCommentsList() throws Exception {
        ItemWithBookingsDto dto = new ItemWithBookingsDto(
                1L, "Item", "Description", true, 2L,
                null, null, List.of()
        );

        String json = objectMapper.writeValueAsString(dto);
        ItemWithBookingsDto result = objectMapper.readValue(json, ItemWithBookingsDto.class);

        assertThat(result.getComments()).isNotNull();
        assertThat(result.getComments()).isEmpty();
    }

    @Test
    void shouldUseNoArgsConstructor() {
        ItemWithBookingsDto dto = new ItemWithBookingsDto();

        assertThat(dto.getId()).isNull();
        assertThat(dto.getName()).isNull();
        assertThat(dto.getDescription()).isNull();
        assertThat(dto.getAvailable()).isNull();
        assertThat(dto.getRequestId()).isNull();
        assertThat(dto.getLastBooking()).isNull();
        assertThat(dto.getNextBooking()).isNull();
        assertThat(dto.getComments()).isNull();
    }

    @Test
    void shouldHandlePartialBookingInfo() throws Exception {
        String json = "{" +
                "\"id\":1," +
                "\"name\":\"Item\"," +
                "\"description\":\"Description\"," +
                "\"available\":true," +
                "\"lastBooking\":{" +
                "   \"id\":1" +
                "}" +
                "}";

        ItemWithBookingsDto dto = objectMapper.readValue(json, ItemWithBookingsDto.class);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getLastBooking()).isNotNull();
        assertThat(dto.getLastBooking().getId()).isEqualTo(1L);
        assertThat(dto.getLastBooking().getBookerId()).isNull();
        assertThat(dto.getLastBooking().getStart()).isNull();
        assertThat(dto.getLastBooking().getEnd()).isNull();
    }
}