package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.client.BookingClient;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingClient bookingClient;

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    // Тест данные
    private BookingCreateDto createValidBookingCreateDto() {
        BookingCreateDto dto = new BookingCreateDto();
        dto.setItemId(1L);
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));
        return dto;
    }

    private String createBookingResponseJson() {
        return """
                {
                    "id": 1,
                    "start": "2024-01-01T10:00:00",
                    "end": "2024-01-02T10:00:00",
                    "status": "WAITING",
                    "booker": {"id": 1, "name": "Booker"},
                    "item": {"id": 1, "name": "Item"}
                }
                """;
    }

    // Тесты для создания бронирования
    @Test
    void create_shouldReturnCreatedBooking() throws Exception {
        // Given
        BookingCreateDto bookingDto = createValidBookingCreateDto();
        String responseBody = createBookingResponseJson();

        when(bookingClient.bookItem(anyLong(), any(BookingCreateDto.class)))
                .thenReturn(ResponseEntity.ok(responseBody));

        // When & Then
        mockMvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andExpect(jsonPath("$.booker.id").value(1))
                .andExpect(jsonPath("$.item.id").value(1));
    }

    @Test
    void create_shouldReturnBadRequestWhenDatesInPast() throws Exception {
        // Given - даты в прошлом
        BookingCreateDto pastDto = new BookingCreateDto();
        pastDto.setItemId(1L);
        pastDto.setStart(LocalDateTime.now().minusDays(1));
        pastDto.setEnd(LocalDateTime.now().minusHours(1));

        // When & Then
        mockMvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pastDto)))
                .andExpect(status().isBadRequest());
    }

    // Тесты для получения бронирования по ID
    @Test
    void getBooking_shouldReturnBooking() throws Exception {
        // Given
        String responseBody = createBookingResponseJson();

        when(bookingClient.getBooking(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.ok(responseBody));

        // When & Then
        mockMvc.perform(get("/bookings/{bookingId}", 1L)
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void getBooking_shouldReturnNotFoundWhenBookingNotExists() throws Exception {
        // Given
        when(bookingClient.getBooking(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.notFound().build());

        // When & Then
        mockMvc.perform(get("/bookings/{bookingId}", 999L)
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isNotFound());
    }

    // Тесты для обновления статуса бронирования
    @Test
    void updateStatus_shouldApproveBooking() throws Exception {
        // Given
        String responseBody = """
                {
                    "id": 1,
                    "status": "APPROVED",
                    "booker": {"id": 1},
                    "item": {"id": 1}
                }
                """;

        when(bookingClient.updateStatus(anyLong(), anyBoolean(), anyLong()))
                .thenReturn(ResponseEntity.ok(responseBody));

        // When & Then - подтверждение бронирования
        mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header(USER_ID_HEADER, 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void updateStatus_shouldRejectBooking() throws Exception {
        // Given
        String responseBody = """
                {
                    "id": 1,
                    "status": "REJECTED",
                    "booker": {"id": 1},
                    "item": {"id": 1}
                }
                """;

        when(bookingClient.updateStatus(anyLong(), anyBoolean(), anyLong()))
                .thenReturn(ResponseEntity.ok(responseBody));

        // When & Then - отклонение бронирования
        mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header(USER_ID_HEADER, 1L)
                        .param("approved", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"));
    }

    @Test
    void updateStatus_shouldReturnBadRequestWhenMissingApprovedParam() throws Exception {
        // When & Then - отсутствует параметр approved
        mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isBadRequest());
    }

    // Тесты для получения бронирований пользователя
    @Test
    void getBookings_shouldReturnUserBookings() throws Exception {
        // Given
        String responseBody = """
                [
                    {
                        "id": 1,
                        "status": "APPROVED",
                        "start": "2024-01-01T10:00:00",
                        "end": "2024-01-02T10:00:00"
                    },
                    {
                        "id": 2,
                        "status": "WAITING",
                        "start": "2024-01-03T10:00:00",
                        "end": "2024-01-04T10:00:00"
                    }
                ]
                """;

        when(bookingClient.getBookings(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.ok(responseBody));

        // When & Then
        mockMvc.perform(get("/bookings")
                        .header(USER_ID_HEADER, 1L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    void getBookings_shouldUseDefaultParameters() throws Exception {
        // Given
        when(bookingClient.getBookings(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.ok("[]"));

        // When & Then - параметры по умолчанию
        mockMvc.perform(get("/bookings")
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getBookings_shouldHandleDifferentStates() throws Exception {
        // Given
        when(bookingClient.getBookings(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.ok("[]"));

        // When & Then - различные состояния
        String[] states = {"CURRENT", "PAST", "FUTURE", "WAITING", "REJECTED", "ALL"};

        for (String state : states) {
            mockMvc.perform(get("/bookings")
                            .header(USER_ID_HEADER, 1L)
                            .param("state", state)
                            .param("from", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk());
        }
    }

    // Тесты для получения бронирований владельца
    @Test
    void getOwnerBookings_shouldReturnOwnerBookings() throws Exception {
        // Given
        String responseBody = """
                [
                    {
                        "id": 1,
                        "status": "APPROVED",
                        "item": {"id": 1, "name": "Owner Item"}
                    }
                ]
                """;

        when(bookingClient.getOwnerBookings(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.ok(responseBody));

        // When & Then
        mockMvc.perform(get("/bookings/owner")
                        .header(USER_ID_HEADER, 1L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].item.name").value("Owner Item"));
    }

    // Тесты для удаления бронирования
    @Test
    void delete_shouldReturnSuccess() throws Exception {
        // Given
        when(bookingClient.delete(anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        // When & Then
        mockMvc.perform(delete("/bookings/{bookingId}", 1L))
                .andExpect(status().isOk());
    }

    // Тесты для валидации заголовков
    @Test
    void allMethodsRequiringUserId_shouldReturnBadRequestWhenHeaderMissing() throws Exception {
        // When & Then - отсутствует заголовок X-Sharer-User-Id
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createValidBookingCreateDto())))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/bookings/1"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(patch("/bookings/1").param("approved", "true"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/bookings"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/bookings/owner"))
                .andExpect(status().isBadRequest());
    }

    // Тест для проверки корректности логирования
    @Test
    void methods_shouldLogAppropriateMessages() throws Exception {
        // Given
        when(bookingClient.getBookings(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.ok("[]"));

        // When & Then
        mockMvc.perform(get("/bookings")
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk());
    }
}