package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.booking.dto.BookingStatus;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingStatusJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldSerializeBookingStatus() throws Exception {
        // Проверяем сериализацию enum в JSON
        String waitingJson = objectMapper.writeValueAsString(BookingStatus.WAITING);
        String approvedJson = objectMapper.writeValueAsString(BookingStatus.APPROVED);

        assertThat(waitingJson).isEqualTo("\"WAITING\"");
        assertThat(approvedJson).isEqualTo("\"APPROVED\"");
    }

    @Test
    void shouldDeserializeBookingStatus() throws Exception {
        // Проверяем десериализацию JSON в enum
        BookingStatus waiting = objectMapper.readValue("\"WAITING\"", BookingStatus.class);
        BookingStatus approved = objectMapper.readValue("\"APPROVED\"", BookingStatus.class);
        BookingStatus rejected = objectMapper.readValue("\"REJECTED\"", BookingStatus.class);
        BookingStatus canceled = objectMapper.readValue("\"CANCELED\"", BookingStatus.class);

        assertThat(waiting).isEqualTo(BookingStatus.WAITING);
        assertThat(approved).isEqualTo(BookingStatus.APPROVED);
        assertThat(rejected).isEqualTo(BookingStatus.REJECTED);
        assertThat(canceled).isEqualTo(BookingStatus.CANCELED);
    }
}