package ru.practicum.shareit.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.booking.dto.BookingCreateDto;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingClientTest {

    @Mock
    private RestTemplate restTemplate;

    private BookingClient bookingClient;

    @BeforeEach
    void setUp() throws Exception {
        // Создаем реальный BookingClient
        bookingClient = new BookingClient("http://localhost:8080",
                new org.springframework.boot.web.client.RestTemplateBuilder());

        // Используем Reflection для установки mock RestTemplate
        Field restField = BaseClient.class.getDeclaredField("rest");
        restField.setAccessible(true);
        restField.set(bookingClient, restTemplate);
    }

    @Test
    void bookItem_ShouldMakePostRequestWithCorrectParameters() {
        // Given
        Long userId = 1L;
        BookingCreateDto requestDto = new BookingCreateDto();
        requestDto.setItemId(2L);
        requestDto.setStart(LocalDateTime.now().plusDays(1));
        requestDto.setEnd(LocalDateTime.now().plusDays(2));

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("booking created");

        when(restTemplate.exchange(
                eq(""),
                eq(org.springframework.http.HttpMethod.POST),
                any(org.springframework.http.HttpEntity.class),
                eq(Object.class)
        )).thenReturn(expectedResponse);

        // When
        ResponseEntity<Object> response = bookingClient.bookItem(userId, requestDto);

        // Then
        assertNotNull(response);
        assertEquals(expectedResponse, response);

        verify(restTemplate).exchange(
                eq(""),
                eq(org.springframework.http.HttpMethod.POST),
                argThat(request -> {
                    // Проверяем, что тело запроса соответствует переданному DTO
                    return requestDto.equals(request.getBody());
                }),
                eq(Object.class)
        );
    }

    @Test
    void getBooking_ShouldMakeGetRequestWithCorrectPath() {
        // Given
        Long userId = 1L;
        Long bookingId = 100L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("booking details");

        when(restTemplate.exchange(
                eq("/" + bookingId),
                eq(org.springframework.http.HttpMethod.GET),
                any(org.springframework.http.HttpEntity.class),
                eq(Object.class)
        )).thenReturn(expectedResponse);

        // When
        ResponseEntity<Object> response = bookingClient.getBooking(userId, bookingId);

        // Then
        assertNotNull(response);
        assertEquals(expectedResponse, response);

        verify(restTemplate).exchange(
                eq("/" + bookingId),
                eq(org.springframework.http.HttpMethod.GET),
                any(org.springframework.http.HttpEntity.class),
                eq(Object.class)
        );
    }

    @Test
    void updateStatus_ShouldMakePatchRequestWithApprovedParameter() {
        // Given
        Long bookingId = 100L;
        Boolean approved = true;
        Long userId = 1L;

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("status updated");

        when(restTemplate.exchange(
                eq("/" + bookingId + "?approved={approved}"),
                eq(org.springframework.http.HttpMethod.PATCH),
                any(org.springframework.http.HttpEntity.class),
                eq(Object.class),
                eq(Map.of("approved", approved))
        )).thenReturn(expectedResponse);

        // When
        ResponseEntity<Object> response = bookingClient.updateStatus(bookingId, approved, userId);

        // Then
        assertNotNull(response);
        assertEquals(expectedResponse, response);

        verify(restTemplate).exchange(
                eq("/" + bookingId + "?approved={approved}"),
                eq(org.springframework.http.HttpMethod.PATCH),
                any(org.springframework.http.HttpEntity.class),
                eq(Object.class),
                eq(Map.of("approved", approved))
        );
    }

    @Test
    void updateStatus_WithFalseApproved_ShouldMakePatchRequestWithFalseParameter() {
        // Given
        Long bookingId = 100L;
        Boolean approved = false;
        Long userId = 1L;

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("status rejected");

        when(restTemplate.exchange(
                eq("/" + bookingId + "?approved={approved}"),
                eq(org.springframework.http.HttpMethod.PATCH),
                any(org.springframework.http.HttpEntity.class),
                eq(Object.class),
                eq(Map.of("approved", approved))
        )).thenReturn(expectedResponse);

        // When
        ResponseEntity<Object> response = bookingClient.updateStatus(bookingId, approved, userId);

        // Then
        assertNotNull(response);
        assertEquals(expectedResponse, response);

        verify(restTemplate).exchange(
                eq("/" + bookingId + "?approved={approved}"),
                eq(org.springframework.http.HttpMethod.PATCH),
                any(org.springframework.http.HttpEntity.class),
                eq(Object.class),
                eq(Map.of("approved", approved))
        );
    }

    @Test
    void getBookings_ShouldMakeGetRequestWithPaginationParameters() {
        // Given
        Long userId = 1L;
        String state = "ALL";
        Integer from = 0;
        Integer size = 10;

        Map<String, Object> expectedParameters = Map.of(
                "state", state,
                "from", from,
                "size", size
        );

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("bookings list");

        when(restTemplate.exchange(
                eq("?state={state}&from={from}&size={size}"),
                eq(org.springframework.http.HttpMethod.GET),
                any(org.springframework.http.HttpEntity.class),
                eq(Object.class),
                eq(expectedParameters)
        )).thenReturn(expectedResponse);

        // When
        ResponseEntity<Object> response = bookingClient.getBookings(userId, state, from, size);

        // Then
        assertNotNull(response);
        assertEquals(expectedResponse, response);

        verify(restTemplate).exchange(
                eq("?state={state}&from={from}&size={size}"),
                eq(org.springframework.http.HttpMethod.GET),
                any(org.springframework.http.HttpEntity.class),
                eq(Object.class),
                eq(expectedParameters)
        );
    }

    @Test
    void getBookings_WithDifferentState_ShouldPassCorrectStateParameter() {
        // Given
        Long userId = 1L;
        String state = "FUTURE";
        Integer from = 5;
        Integer size = 20;

        Map<String, Object> expectedParameters = Map.of(
                "state", state,
                "from", from,
                "size", size
        );

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("future bookings");

        when(restTemplate.exchange(
                eq("?state={state}&from={from}&size={size}"),
                eq(org.springframework.http.HttpMethod.GET),
                any(org.springframework.http.HttpEntity.class),
                eq(Object.class),
                eq(expectedParameters)
        )).thenReturn(expectedResponse);

        // When
        ResponseEntity<Object> response = bookingClient.getBookings(userId, state, from, size);

        // Then
        assertNotNull(response);
        assertEquals(expectedResponse, response);

        verify(restTemplate).exchange(
                eq("?state={state}&from={from}&size={size}"),
                eq(org.springframework.http.HttpMethod.GET),
                any(org.springframework.http.HttpEntity.class),
                eq(Object.class),
                eq(expectedParameters)
        );
    }

    @Test
    void getOwnerBookings_ShouldMakeGetRequestToOwnerEndpoint() {
        // Given
        Long userId = 1L;
        String state = "WAITING";
        Integer from = 0;
        Integer size = 10;

        Map<String, Object> expectedParameters = Map.of(
                "state", state,
                "from", from,
                "size", size
        );

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("owner bookings");

        when(restTemplate.exchange(
                eq("/owner?state={state}&from={from}&size={size}"),
                eq(org.springframework.http.HttpMethod.GET),
                any(org.springframework.http.HttpEntity.class),
                eq(Object.class),
                eq(expectedParameters)
        )).thenReturn(expectedResponse);

        // When
        ResponseEntity<Object> response = bookingClient.getOwnerBookings(userId, state, from, size);

        // Then
        assertNotNull(response);
        assertEquals(expectedResponse, response);

        verify(restTemplate).exchange(
                eq("/owner?state={state}&from={from}&size={size}"),
                eq(org.springframework.http.HttpMethod.GET),
                any(org.springframework.http.HttpEntity.class),
                eq(Object.class),
                eq(expectedParameters)
        );
    }

    @Test
    void delete_ShouldMakeDeleteRequest() {
        // Given
        Long bookingId = 100L;

        ResponseEntity<Object> expectedResponse = ResponseEntity.noContent().build();

        when(restTemplate.exchange(
                eq("/" + bookingId),
                eq(org.springframework.http.HttpMethod.DELETE),
                any(org.springframework.http.HttpEntity.class),
                eq(Object.class)
        )).thenReturn(expectedResponse);

        // When
        ResponseEntity<Object> response = bookingClient.delete(bookingId);

        // Then
        assertNotNull(response);
        assertEquals(expectedResponse, response);

        verify(restTemplate).exchange(
                eq("/" + bookingId),
                eq(org.springframework.http.HttpMethod.DELETE),
                any(org.springframework.http.HttpEntity.class),
                eq(Object.class)
        );
    }


    @Test
    void getOwnerBookings_WithCustomPagination_ShouldPassCorrectParameters() {
        // Given
        Long userId = 1L;
        String state = "PAST";
        Integer from = 10;
        Integer size = 5;

        Map<String, Object> expectedParameters = Map.of(
                "state", state,
                "from", from,
                "size", size
        );

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("past owner bookings");

        when(restTemplate.exchange(
                eq("/owner?state={state}&from={from}&size={size}"),
                eq(org.springframework.http.HttpMethod.GET),
                any(org.springframework.http.HttpEntity.class),
                eq(Object.class),
                eq(expectedParameters)
        )).thenReturn(expectedResponse);

        // When
        ResponseEntity<Object> response = bookingClient.getOwnerBookings(userId, state, from, size);

        // Then
        assertNotNull(response);
        assertEquals(expectedResponse, response);

        verify(restTemplate).exchange(
                eq("/owner?state={state}&from={from}&size={size}"),
                eq(org.springframework.http.HttpMethod.GET),
                any(org.springframework.http.HttpEntity.class),
                eq(Object.class),
                eq(expectedParameters)
        );
    }

    @Test
    void constructor_ShouldInitializeWithCorrectBaseUrl() {
        // Given
        String serverUrl = "http://test-server:9090";

        // When
        BookingClient client = new BookingClient(serverUrl,
                new org.springframework.boot.web.client.RestTemplateBuilder());

        // Then - проверяем что клиент создан (не падает с исключениями)
        assertNotNull(client);
    }

    @Test
    void bookItem_WithNullRequestBody_ShouldHandleNullBody() {
        // Given
        Long userId = 1L;
        BookingCreateDto requestDto = null;

        ResponseEntity<Object> expectedResponse = ResponseEntity.badRequest().build();

        when(restTemplate.exchange(
                eq(""),
                eq(org.springframework.http.HttpMethod.POST),
                any(org.springframework.http.HttpEntity.class),
                eq(Object.class)
        )).thenReturn(expectedResponse);

        // When
        ResponseEntity<Object> response = bookingClient.bookItem(userId, requestDto);

        // Then
        assertNotNull(response);
        assertEquals(expectedResponse, response);

        verify(restTemplate).exchange(
                eq(""),
                eq(org.springframework.http.HttpMethod.POST),
                argThat(request -> request.getBody() == null),
                eq(Object.class)
        );
    }
}