package ru.practicum.shareit.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemClientTest {

    @Mock
    private RestTemplate restTemplate;

    private ItemClient itemClient;

    @BeforeEach
    void setUp() throws Exception {
        // Создаем реальный ItemClient
        itemClient = new ItemClient("http://localhost:8080",
                new org.springframework.boot.web.client.RestTemplateBuilder());

        // Используем Reflection для установки mock RestTemplate
        Field restField = BaseClient.class.getDeclaredField("rest");
        restField.setAccessible(true);
        restField.set(itemClient, restTemplate);
    }

    @Test
    void create_ShouldMakePostRequestWithItemDto() {
        // Given
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);

        Long ownerId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("item created");

        when(restTemplate.exchange(
                eq(""),
                eq(org.springframework.http.HttpMethod.POST),
                any(org.springframework.http.HttpEntity.class),
                eq(Object.class)
        )).thenReturn(expectedResponse);

        // When
        ResponseEntity<Object> response = itemClient.create(itemDto, ownerId);

        // Then
        assertNotNull(response);
        assertEquals(expectedResponse, response);

        verify(restTemplate).exchange(
                eq(""),
                eq(org.springframework.http.HttpMethod.POST),
                argThat(request ->
                        itemDto.equals(request.getBody())),
                eq(Object.class)
        );
    }

    @Test
    void getById_ShouldMakeGetRequestWithCorrectPath() {
        // Given
        Long itemId = 100L;
        Long userId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("item details");

        when(restTemplate.exchange(
                eq("/" + itemId),
                eq(org.springframework.http.HttpMethod.GET),
                any(org.springframework.http.HttpEntity.class),
                eq(Object.class)
        )).thenReturn(expectedResponse);

        // When
        ResponseEntity<Object> response = itemClient.getById(itemId, userId);

        // Then
        assertNotNull(response);
        assertEquals(expectedResponse, response);

        verify(restTemplate).exchange(
                eq("/" + itemId),
                eq(org.springframework.http.HttpMethod.GET),
                any(org.springframework.http.HttpEntity.class),
                eq(Object.class)
        );
    }

    @Test
    void getByOwnerId_ShouldMakeGetRequestWithPaginationParameters() {
        // Given
        Long ownerId = 1L;
        Integer from = 0;
        Integer size = 10;

        Map<String, Object> expectedParameters = Map.of(
                "from", from,
                "size", size
        );

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("owner items");

        when(restTemplate.exchange(
                eq("?from={from}&size={size}"),
                eq(org.springframework.http.HttpMethod.GET),
                any(org.springframework.http.HttpEntity.class),
                eq(Object.class),
                eq(expectedParameters)
        )).thenReturn(expectedResponse);

        // When
        ResponseEntity<Object> response = itemClient.getByOwnerId(ownerId, from, size);

        // Then
        assertNotNull(response);
        assertEquals(expectedResponse, response);

        verify(restTemplate).exchange(
                eq("?from={from}&size={size}"),
                eq(org.springframework.http.HttpMethod.GET),
                any(org.springframework.http.HttpEntity.class),
                eq(Object.class),
                eq(expectedParameters)
        );
    }

    @Test
    void getByOwnerId_WithCustomPagination_ShouldPassCorrectParameters() {
        // Given
        Long ownerId = 1L;
        Integer from = 5;
        Integer size = 20;

        Map<String, Object> expectedParameters = Map.of(
                "from", from,
                "size", size
        );

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("owner items with custom pagination");

        when(restTemplate.exchange(
                eq("?from={from}&size={size}"),
                eq(org.springframework.http.HttpMethod.GET),
                any(org.springframework.http.HttpEntity.class),
                eq(Object.class),
                eq(expectedParameters)
        )).thenReturn(expectedResponse);

        // When
        ResponseEntity<Object> response = itemClient.getByOwnerId(ownerId, from, size);

        // Then
        assertNotNull(response);
        assertEquals(expectedResponse, response);

        verify(restTemplate).exchange(
                eq("?from={from}&size={size}"),
                eq(org.springframework.http.HttpMethod.GET),
                any(org.springframework.http.HttpEntity.class),
                eq(Object.class),
                eq(expectedParameters)
        );
    }

    @Test
    void update_ShouldMakePatchRequestWithItemDto() {
        // Given
        Long itemId = 100L;
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Updated Name");
        itemDto.setDescription("Updated Description");

        Long ownerId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("item updated");

        when(restTemplate.exchange(
                eq("/" + itemId),
                eq(org.springframework.http.HttpMethod.PATCH),
                any(org.springframework.http.HttpEntity.class),
                eq(Object.class)
        )).thenReturn(expectedResponse);

        // When
        ResponseEntity<Object> response = itemClient.update(itemId, itemDto, ownerId);

        // Then
        assertNotNull(response);
        assertEquals(expectedResponse, response);

        verify(restTemplate).exchange(
                eq("/" + itemId),
                eq(org.springframework.http.HttpMethod.PATCH),
                argThat(request ->
                        itemDto.equals(request.getBody())),
                eq(Object.class)
        );
    }

    @Test
    void delete_ShouldMakeDeleteRequest() {
        // Given
        Long itemId = 100L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.noContent().build();

        when(restTemplate.exchange(
                eq("/" + itemId),
                eq(org.springframework.http.HttpMethod.DELETE),
                any(org.springframework.http.HttpEntity.class),
                eq(Object.class)
        )).thenReturn(expectedResponse);

        // When
        ResponseEntity<Object> response = itemClient.delete(itemId);

        // Then
        assertNotNull(response);
        assertEquals(expectedResponse, response);

        verify(restTemplate).exchange(
                eq("/" + itemId),
                eq(org.springframework.http.HttpMethod.DELETE),
                any(org.springframework.http.HttpEntity.class),
                eq(Object.class)
        );
    }

    @Test
    void search_ShouldMakeGetRequestWithSearchParameters() {
        // Given
        String searchText = "drill";
        Integer from = 0;
        Integer size = 10;

        Map<String, Object> expectedParameters = Map.of(
                "text", searchText,
                "from", from,
                "size", size
        );

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("search results");

        when(restTemplate.exchange(
                eq("/search?text={text}&from={from}&size={size}"),
                eq(org.springframework.http.HttpMethod.GET),
                any(org.springframework.http.HttpEntity.class),
                eq(Object.class),
                eq(expectedParameters)
        )).thenReturn(expectedResponse);

        // When
        ResponseEntity<Object> response = itemClient.search(searchText, from, size);

        // Then
        assertNotNull(response);
        assertEquals(expectedResponse, response);

        verify(restTemplate).exchange(
                eq("/search?text={text}&from={from}&size={size}"),
                eq(org.springframework.http.HttpMethod.GET),
                any(org.springframework.http.HttpEntity.class),
                eq(Object.class),
                eq(expectedParameters)
        );
    }

    @Test
    void search_WithEmptyText_ShouldPassEmptyTextParameter() {
        // Given
        String searchText = "";
        Integer from = 0;
        Integer size = 10;

        Map<String, Object> expectedParameters = Map.of(
                "text", searchText,
                "from", from,
                "size", size
        );

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("empty search results");

        when(restTemplate.exchange(
                eq("/search?text={text}&from={from}&size={size}"),
                eq(org.springframework.http.HttpMethod.GET),
                any(org.springframework.http.HttpEntity.class),
                eq(Object.class),
                eq(expectedParameters)
        )).thenReturn(expectedResponse);

        // When
        ResponseEntity<Object> response = itemClient.search(searchText, from, size);

        // Then
        assertNotNull(response);
        assertEquals(expectedResponse, response);

        verify(restTemplate).exchange(
                eq("/search?text={text}&from={from}&size={size}"),
                eq(org.springframework.http.HttpMethod.GET),
                any(org.springframework.http.HttpEntity.class),
                eq(Object.class),
                eq(expectedParameters)
        );
    }

    @Test
    void search_WithNullUserId_ShouldNotIncludeUserIdHeader() {
        // Given
        String searchText = "test";
        Integer from = 0;
        Integer size = 10;

        Map<String, Object> expectedParameters = Map.of(
                "text", searchText,
                "from", from,
                "size", size
        );

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("search without user");

        when(restTemplate.exchange(
                eq("/search?text={text}&from={from}&size={size}"),
                eq(org.springframework.http.HttpMethod.GET),
                any(org.springframework.http.HttpEntity.class),
                eq(Object.class),
                eq(expectedParameters)
        )).thenReturn(expectedResponse);

        // When
        ResponseEntity<Object> response = itemClient.search(searchText, from, size);

        // Then
        assertNotNull(response);
        assertEquals(expectedResponse, response);

        verify(restTemplate).exchange(
                eq("/search?text={text}&from={from}&size={size}"),
                eq(org.springframework.http.HttpMethod.GET),
                argThat(request ->
                        !request.getHeaders().containsKey("X-Sharer-User-Id")),
                eq(Object.class),
                eq(expectedParameters)
        );
    }

    @Test
    void addComment_ShouldMakePostRequestToCommentEndpoint() {
        // Given
        Long itemId = 100L;
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Great item!");

        Long userId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("comment added");

        when(restTemplate.exchange(
                eq("/" + itemId + "/comment"),
                eq(org.springframework.http.HttpMethod.POST),
                any(org.springframework.http.HttpEntity.class),
                eq(Object.class)
        )).thenReturn(expectedResponse);

        // When
        ResponseEntity<Object> response = itemClient.addComment(itemId, commentDto, userId);

        // Then
        assertNotNull(response);
        assertEquals(expectedResponse, response);

        verify(restTemplate).exchange(
                eq("/" + itemId + "/comment"),
                eq(org.springframework.http.HttpMethod.POST),
                argThat(request ->
                        commentDto.equals(request.getBody())),
                eq(Object.class)
        );
    }

    @Test
    void addComment_WithNullComment_ShouldHandleNullBody() {
        // Given
        Long itemId = 100L;
        CommentDto commentDto = null;
        Long userId = 1L;

        ResponseEntity<Object> expectedResponse = ResponseEntity.badRequest().build();

        when(restTemplate.exchange(
                eq("/" + itemId + "/comment"),
                eq(org.springframework.http.HttpMethod.POST),
                any(org.springframework.http.HttpEntity.class),
                eq(Object.class)
        )).thenReturn(expectedResponse);

        // When
        ResponseEntity<Object> response = itemClient.addComment(itemId, commentDto, userId);

        // Then
        assertNotNull(response);
        assertEquals(expectedResponse, response);

        verify(restTemplate).exchange(
                eq("/" + itemId + "/comment"),
                eq(org.springframework.http.HttpMethod.POST),
                argThat(request -> request.getBody() == null),
                eq(Object.class)
        );
    }


    @Test
    void constructor_ShouldInitializeWithCorrectBaseUrl() {
        // Given
        String serverUrl = "http://test-server:9090";

        // When
        ItemClient client = new ItemClient(serverUrl,
                new org.springframework.boot.web.client.RestTemplateBuilder());

        // Then - проверяем что клиент создан (не падает с исключениями)
        assertNotNull(client);
    }
}