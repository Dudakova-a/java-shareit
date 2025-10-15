package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingCreateDto;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
class BookingIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Тест данные
    private BookingCreateDto createValidBookingDto() {
        return new BookingCreateDto(
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                1L
        );
    }

    // Тесты валидации создания бронирования
    @Test
    void createBooking_withValidData_shouldReturnCreated() throws Exception {
        // Given - сначала нужно создать пользователя и вещь
        // (предполагаем, что в вашем AbstractIntegrationTest есть методы для этого)

        BookingCreateDto bookingDto = createValidBookingDto();

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void createBooking_withNullStart_shouldReturnBadRequest() throws Exception {
        BookingCreateDto bookingDto = new BookingCreateDto(
                null, // start is null
                LocalDateTime.now().plusDays(2),
                1L
        );

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createBooking_withNullEnd_shouldReturnBadRequest() throws Exception {
        BookingCreateDto bookingDto = new BookingCreateDto(
                LocalDateTime.now().plusDays(1),
                null, // end is null
                1L
        );

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createBooking_withNullItemId_shouldReturnBadRequest() throws Exception {
        BookingCreateDto bookingDto = new BookingCreateDto(
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                null // itemId is null
        );

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createBooking_withStartInPast_shouldReturnBadRequest() throws Exception {
        BookingCreateDto bookingDto = new BookingCreateDto(
                LocalDateTime.now().minusDays(1), // start in past
                LocalDateTime.now().plusDays(2),
                1L
        );

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createBooking_withEndInPast_shouldReturnBadRequest() throws Exception {
        BookingCreateDto bookingDto = new BookingCreateDto(
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().minusDays(1), // end in past
                1L
        );

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isBadRequest());
    }


    @Test
    void createBooking_withoutUserIdHeader_shouldReturnBadRequest() throws Exception {
        BookingCreateDto bookingDto = createValidBookingDto();

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isBadRequest());
    }


    @Test
    void getBooking_withNonExistingId_shouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/bookings/{bookingId}", 999L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void getBooking_withoutUserIdHeader_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/bookings/{bookingId}", 1L))
                .andExpect(status().isBadRequest());
    }


    @Test
    void updateStatus_withoutApprovedParam_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isBadRequest());
    }

    // Тесты для получения списка бронирований
    @Test
    void getBookings_withDefaultParams_shouldReturnList() throws Exception {
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getBookings_withStateParam_shouldReturnFilteredList() throws Exception {
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    // Тесты для получения бронирований владельца
    @Test
    void getOwnerBookings_shouldReturnOwnerBookings() throws Exception {
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

}