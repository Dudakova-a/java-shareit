package ru.practicum.shareit.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestClientTest {

    @Mock
    private RestTemplate restTemplate;

    private ItemRequestClient itemRequestClient;

    @BeforeEach
    void setUp() throws Exception {
        // Создаем реальный ItemRequestClient
        itemRequestClient = new ItemRequestClient("http://localhost:8080",
                new org.springframework.boot.web.client.RestTemplateBuilder());

        // Используем Reflection для установки mock RestTemplate
        Field restField = BaseClient.class.getDeclaredField("rest");
        restField.setAccessible(true);
        restField.set(itemRequestClient, restTemplate);
    }

    @Test
    void create_ShouldMakePostRequestWithItemRequestCreateDto() {
        // Given
        ItemRequestCreateDto createDto = new ItemRequestCreateDto();
        createDto.setDescription("Need a power drill");

        Long userId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("request created");

        when(restTemplate.exchange(
                eq(""),
                eq(org.springframework.http.HttpMethod.POST),
                any(org.springframework.http.HttpEntity.class),
                eq(Object.class)
        )).thenReturn(expectedResponse);

        // When
        ResponseEntity<Object> response = itemRequestClient.create(createDto, userId);

        // Then
        assertNotNull(response);
        assertEquals(expectedResponse, response);

        verify(restTemplate).exchange(
                eq(""),
                eq(org.springframework.http.HttpMethod.POST),
                argThat(request ->
                        createDto.equals(request.getBody())),
                eq(Object.class)
        );
    }

    @Test
    void getByUserId_ShouldMakeGetRequestWithoutAdditionalPath() {
        // Given
        Long userId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("user requests");

        when(restTemplate.exchange(
                eq(""),
                eq(org.springframework.http.HttpMethod.GET),
                any(org.springframework.http.HttpEntity.class),
                eq(Object.class)
        )).thenReturn(expectedResponse);

        // When
        ResponseEntity<Object> response = itemRequestClient.getByUserId(userId);

        // Then
        assertNotNull(response);
        assertEquals(expectedResponse, response);

        verify(restTemplate).exchange(
                eq(""),
                eq(org.springframework.http.HttpMethod.GET),
                any(org.springframework.http.HttpEntity.class),
                eq(Object.class)
        );
    }

    @Test
    void getAllExceptUser_ShouldMakeGetRequestWithPaginationParameters() {
        // Given
        Long userId = 1L;
        Integer from = 0;
        Integer size = 10;

        Map<String, Object> expectedParameters = Map.of(
                "from", from,
                "size", size
        );

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("other users requests");

        when(restTemplate.exchange(
                eq("/all?from={from}&size={size}"),
                eq(org.springframework.http.HttpMethod.GET),
                any(org.springframework.http.HttpEntity.class),
                eq(Object.class),
                eq(expectedParameters)
        )).thenReturn(expectedResponse);

        // When
        ResponseEntity<Object> response = itemRequestClient.getAllExceptUser(userId, from, size);

        // Then
        assertNotNull(response);
        assertEquals(expectedResponse, response);

        verify(restTemplate).exchange(
                eq("/all?from={from}&size={size}"),
                eq(org.springframework.http.HttpMethod.GET),
                any(org.springframework.http.HttpEntity.class),
                eq(Object.class),
                eq(expectedParameters)
        );
    }

    @Test
    void getAllExceptUser_WithCustomPagination_ShouldPassCorrectParameters() {
        // Given
        Long userId = 1L;
        Integer from = 5;
        Integer size = 20;

        Map<String, Object> expectedParameters = Map.of(
                "from", from,
                "size", size
        );

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("custom pagination requests");

        when(restTemplate.exchange(
                eq("/all?from={from}&size={size}"),
                eq(org.springframework.http.HttpMethod.GET),
                any(org.springframework.http.HttpEntity.class),
                eq(Object.class),
                eq(expectedParameters)
        )).thenReturn(expectedResponse);

        // When
        ResponseEntity<Object> response = itemRequestClient.getAllExceptUser(userId, from, size);

        // Then
        assertNotNull(response);
        assertEquals(expectedResponse, response);

        verify(restTemplate).exchange(
                eq("/all?from={from}&size={size}"),
                eq(org.springframework.http.HttpMethod.GET),
                any(org.springframework.http.HttpEntity.class),
                eq(Object.class),
                eq(expectedParameters)
        );
    }

    @Test
    void getById_ShouldMakeGetRequestWithRequestIdPath() {
        // Given
        Long requestId = 100L;
        Long userId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("request details");

        when(restTemplate.exchange(
                eq("/" + requestId),
                eq(org.springframework.http.HttpMethod.GET),
                any(org.springframework.http.HttpEntity.class),
                eq(Object.class)
        )).thenReturn(expectedResponse);

        // When
        ResponseEntity<Object> response = itemRequestClient.getById(requestId, userId);

        // Then
        assertNotNull(response);
        assertEquals(expectedResponse, response);

        verify(restTemplate).exchange(
                eq("/" + requestId),
                eq(org.springframework.http.HttpMethod.GET),
                any(org.springframework.http.HttpEntity.class),
                eq(Object.class)
        );
    }

    @Test
    void update_ShouldMakePatchRequestWithItemRequestDto() {
        // Given
        Long requestId = 100L;
        ItemRequestDto updateDto = new ItemRequestDto();
        updateDto.setDescription("Updated description");

        Long userId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("request updated");

        when(restTemplate.exchange(
                eq("/" + requestId),
                eq(org.springframework.http.HttpMethod.PATCH),
                any(org.springframework.http.HttpEntity.class),
                eq(Object.class)
        )).thenReturn(expectedResponse);

        // When
        ResponseEntity<Object> response = itemRequestClient.update(requestId, updateDto, userId);

        // Then
        assertNotNull(response);
        assertEquals(expectedResponse, response);

        verify(restTemplate).exchange(
                eq("/" + requestId),
                eq(org.springframework.http.HttpMethod.PATCH),
                argThat(request ->
                        updateDto.equals(request.getBody())),
                eq(Object.class)
        );
    }

    @Test
    void delete_ShouldMakeDeleteRequestWithRequestId() {
        // Given
        Long requestId = 100L;
        Long userId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.noContent().build();

        when(restTemplate.exchange(
                eq("/" + requestId),
                eq(org.springframework.http.HttpMethod.DELETE),
                any(org.springframework.http.HttpEntity.class),
                eq(Object.class)
        )).thenReturn(expectedResponse);

        // When
        ResponseEntity<Object> response = itemRequestClient.delete(requestId, userId);

        // Then
        assertNotNull(response);
        assertEquals(expectedResponse, response);

        verify(restTemplate).exchange(
                eq("/" + requestId),
                eq(org.springframework.http.HttpMethod.DELETE),
                any(org.springframework.http.HttpEntity.class),
                eq(Object.class)
        );
    }

    @Test
    void create_WithNullRequestBody_ShouldHandleNullBody() {
        // Given
        ItemRequestCreateDto createDto = null;
        Long userId = 1L;

        ResponseEntity<Object> expectedResponse = ResponseEntity.badRequest().build();

        when(restTemplate.exchange(
                eq(""),
                eq(org.springframework.http.HttpMethod.POST),
                any(org.springframework.http.HttpEntity.class),
                eq(Object.class)
        )).thenReturn(expectedResponse);

        // When
        ResponseEntity<Object> response = itemRequestClient.create(createDto, userId);

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

    @Test
    void update_WithNullRequestBody_ShouldHandleNullBody() {
        // Given
        Long requestId = 100L;
        ItemRequestDto updateDto = null;
        Long userId = 1L;

        ResponseEntity<Object> expectedResponse = ResponseEntity.badRequest().build();

        when(restTemplate.exchange(
                eq("/" + requestId),
                eq(org.springframework.http.HttpMethod.PATCH),
                any(org.springframework.http.HttpEntity.class),
                eq(Object.class)
        )).thenReturn(expectedResponse);

        // When
        ResponseEntity<Object> response = itemRequestClient.update(requestId, updateDto, userId);

        // Then
        assertNotNull(response);
        assertEquals(expectedResponse, response);

        verify(restTemplate).exchange(
                eq("/" + requestId),
                eq(org.springframework.http.HttpMethod.PATCH),
                argThat(request -> request.getBody() == null),
                eq(Object.class)
        );
    }


    @Test
    void getAllExceptUser_WithZeroPagination_ShouldPassZeroValues() {
        // Given
        Long userId = 1L;
        Integer from = 0;
        Integer size = 0;

        Map<String, Object> expectedParameters = Map.of(
                "from", from,
                "size", size
        );

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("requests with zero pagination");

        when(restTemplate.exchange(
                eq("/all?from={from}&size={size}"),
                eq(org.springframework.http.HttpMethod.GET),
                any(org.springframework.http.HttpEntity.class),
                eq(Object.class),
                eq(expectedParameters)
        )).thenReturn(expectedResponse);

        // When
        ResponseEntity<Object> response = itemRequestClient.getAllExceptUser(userId, from, size);

        // Then
        assertNotNull(response);
        assertEquals(expectedResponse, response);

        verify(restTemplate).exchange(
                eq("/all?from={from}&size={size}"),
                eq(org.springframework.http.HttpMethod.GET),
                any(org.springframework.http.HttpEntity.class),
                eq(Object.class),
                eq(expectedParameters)
        );
    }

    @Test
    void getByUserId_ShouldIncludeUserIdHeader() {
        // Given
        Long userId = 123L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("user requests");

        when(restTemplate.exchange(
                eq(""),
                eq(org.springframework.http.HttpMethod.GET),
                any(org.springframework.http.HttpEntity.class),
                eq(Object.class)
        )).thenReturn(expectedResponse);

        // When
        ResponseEntity<Object> response = itemRequestClient.getByUserId(userId);

        // Then
        verify(restTemplate).exchange(
                eq(""),
                eq(org.springframework.http.HttpMethod.GET),
                argThat(request -> {
                    org.springframework.http.HttpHeaders headers = request.getHeaders();
                    return headers.containsKey("X-Sharer-User-Id") &&
                            "123".equals(headers.getFirst("X-Sharer-User-Id"));
                }),
                eq(Object.class)
        );
    }

    @Test
    void getById_ShouldIncludeUserIdHeader() {
        // Given
        Long requestId = 100L;
        Long userId = 456L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("request details");

        when(restTemplate.exchange(
                eq("/" + requestId),
                eq(org.springframework.http.HttpMethod.GET),
                any(org.springframework.http.HttpEntity.class),
                eq(Object.class)
        )).thenReturn(expectedResponse);

        // When
        ResponseEntity<Object> response = itemRequestClient.getById(requestId, userId);

        // Then
        verify(restTemplate).exchange(
                eq("/" + requestId),
                eq(org.springframework.http.HttpMethod.GET),
                argThat(request -> {
                    org.springframework.http.HttpHeaders headers = request.getHeaders();
                    return headers.containsKey("X-Sharer-User-Id") &&
                            "456".equals(headers.getFirst("X-Sharer-User-Id"));
                }),
                eq(Object.class)
        );
    }

    @Test
    void constructor_ShouldInitializeWithCorrectBaseUrl() {
        // Given
        String serverUrl = "http://test-server:9090";

        // When
        ItemRequestClient client = new ItemRequestClient(serverUrl,
                new org.springframework.boot.web.client.RestTemplateBuilder());

        // Then - проверяем что клиент создан (не падает с исключениями)
        assertNotNull(client);
    }

    @Test
    void delete_ShouldIncludeUserIdHeader() {
        // Given
        Long requestId = 100L;
        Long userId = 789L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.noContent().build();

        when(restTemplate.exchange(
                eq("/" + requestId),
                eq(org.springframework.http.HttpMethod.DELETE),
                any(org.springframework.http.HttpEntity.class),
                eq(Object.class)
        )).thenReturn(expectedResponse);

        // When
        ResponseEntity<Object> response = itemRequestClient.delete(requestId, userId);

        // Then
        verify(restTemplate).exchange(
                eq("/" + requestId),
                eq(org.springframework.http.HttpMethod.DELETE),
                argThat(request -> {
                    org.springframework.http.HttpHeaders headers = request.getHeaders();
                    return headers.containsKey("X-Sharer-User-Id") &&
                            "789".equals(headers.getFirst("X-Sharer-User-Id"));
                }),
                eq(Object.class)
        );
    }
}