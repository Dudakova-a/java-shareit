package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldSerializeBookingDto() throws Exception {
        BookingDto.Booker booker = BookingDto.Booker.builder()
                .id(1L)
                .name("Booker Name")
                .email("booker@mail.com")
                .build();

        BookingDto.Item item = BookingDto.Item.builder()
                .id(1L)
                .name("Item Name")
                .description("Item Description")
                .available(true)
                .requestId(2L)
                .build();

        BookingDto dto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2024, 1, 1, 10, 0))
                .end(LocalDateTime.of(2024, 1, 2, 10, 0))
                .itemId(1L)
                .bookerId(1L)
                .status("APPROVED")
                .booker(booker)
                .item(item)
                .build();

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"start\":");
        assertThat(json).contains("\"end\":");
        assertThat(json).contains("\"itemId\":1");
        assertThat(json).contains("\"bookerId\":1");
        assertThat(json).contains("\"status\":\"APPROVED\"");
        assertThat(json).contains("\"booker\"");
        assertThat(json).contains("\"item\"");
    }

    @Test
    void shouldDeserializeBookingDto() throws Exception {
        String json = "{" +
                "\"id\":1," +
                "\"start\":\"2024-01-01T10:00:00\"," +
                "\"end\":\"2024-01-02T10:00:00\"," +
                "\"itemId\":1," +
                "\"bookerId\":1," +
                "\"status\":\"APPROVED\"," +
                "\"booker\":{" +
                "\"id\":1," +
                "\"name\":\"Booker Name\"," +
                "\"email\":\"booker@mail.com\"" +
                "}," +
                "\"item\":{" +
                "\"id\":1," +
                "\"name\":\"Item Name\"," +
                "\"description\":\"Item Description\"," +
                "\"available\":true," +
                "\"requestId\":2" +
                "}" +
                "}";

        BookingDto dto = objectMapper.readValue(json, BookingDto.class);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getStart()).isEqualTo(LocalDateTime.of(2024, 1, 1, 10, 0));
        assertThat(dto.getEnd()).isEqualTo(LocalDateTime.of(2024, 1, 2, 10, 0));
        assertThat(dto.getItemId()).isEqualTo(1L);
        assertThat(dto.getBookerId()).isEqualTo(1L);
        assertThat(dto.getStatus()).isEqualTo("APPROVED");
        assertThat(dto.getBooker().getId()).isEqualTo(1L);
        assertThat(dto.getBooker().getName()).isEqualTo("Booker Name");
        assertThat(dto.getItem().getId()).isEqualTo(1L);
        assertThat(dto.getItem().getName()).isEqualTo("Item Name");
        assertThat(dto.getItem().getAvailable()).isTrue();
    }
}