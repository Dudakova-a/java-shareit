package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.item.dto.BookingInfoDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingInfoDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldSerializeBookingInfoDto() throws Exception {
        BookingInfoDto dto = new BookingInfoDto();
        dto.setId(1L);
        dto.setBookerId(2L);
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime end = LocalDateTime.of(2024, 1, 2, 10, 0);
        dto.setStart(start);
        dto.setEnd(end);

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"bookerId\":2");
        assertThat(json).contains("\"start\":\"2024-01-01T10:00:00\"");
        assertThat(json).contains("\"end\":\"2024-01-02T10:00:00\"");
    }

    @Test
    void shouldDeserializeBookingInfoDto() throws Exception {
        String json = "{" +
                "\"id\":1," +
                "\"bookerId\":2," +
                "\"start\":\"2024-01-01T10:00:00\"," +
                "\"end\":\"2024-01-02T10:00:00\"" +
                "}";

        BookingInfoDto dto = objectMapper.readValue(json, BookingInfoDto.class);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getBookerId()).isEqualTo(2L);
        assertThat(dto.getStart()).isEqualTo(LocalDateTime.of(2024, 1, 1, 10, 0));
        assertThat(dto.getEnd()).isEqualTo(LocalDateTime.of(2024, 1, 2, 10, 0));
    }

    @Test
    void shouldHandleAllArgsConstructor() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        BookingInfoDto dto = new BookingInfoDto(1L, 2L, start, end);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getBookerId()).isEqualTo(2L);
        assertThat(dto.getStart()).isEqualTo(start);
        assertThat(dto.getEnd()).isEqualTo(end);
    }

    @Test
    void shouldHandleNoArgsConstructor() {
        BookingInfoDto dto = new BookingInfoDto();

        assertThat(dto.getId()).isNull();
        assertThat(dto.getBookerId()).isNull();
        assertThat(dto.getStart()).isNull();
        assertThat(dto.getEnd()).isNull();
    }
}