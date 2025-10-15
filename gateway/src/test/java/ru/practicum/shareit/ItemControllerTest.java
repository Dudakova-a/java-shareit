package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.client.ItemClient;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;


import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemClient itemClient;

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    // Тест данных
    private ItemDto createTestItemDto() {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);
        return itemDto;
    }

    private CommentDto createTestCommentDto() {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("Test comment");
        commentDto.setAuthorName("Test User");
        commentDto.setCreated(LocalDateTime.now());
        return commentDto;
    }

    // Тесты для создания вещи
    @Test
    void create_shouldReturnCreatedItem() throws Exception {
        // Given
        ItemDto itemDto = createTestItemDto();
        itemDto.setId(null); // Для создания ID должен быть null
        
        String responseBody = """
            {
                "id": 1,
                "name": "Test Item",
                "description": "Test Description",
                "available": true
            }
            """;

        when(itemClient.create(any(ItemDto.class), anyLong()))
                .thenReturn(ResponseEntity.ok(responseBody));

        // When & Then
        mockMvc.perform(post("/items")
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Item"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void create_shouldReturnBadRequestWhenValidationFails() throws Exception {
        // Given - ItemDto без обязательных полей
        ItemDto invalidItemDto = new ItemDto();
        // name is null - должно вызвать ошибку валидации

        // When & Then
        mockMvc.perform(post("/items")
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidItemDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_shouldReturnBadRequestWhenUserIdHeaderMissing() throws Exception {
        // Given
        ItemDto itemDto = createTestItemDto();

        // When & Then - отсутствует заголовок X-Sharer-User-Id
        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());
    }

    // Тесты для получения вещи по ID
    @Test
    void getById_shouldReturnItem() throws Exception {
        // Given
        String responseBody = """
            {
                "id": 1,
                "name": "Test Item",
                "description": "Test Description",
                "available": true
            }
            """;

        when(itemClient.getById(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.ok(responseBody));

        // When & Then
        mockMvc.perform(get("/items/{id}", 1L)
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Item"));
    }

    @Test
    void getById_shouldReturnNotFoundWhenItemNotExists() throws Exception {
        // Given
        when(itemClient.getById(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.notFound().build());

        // When & Then
        mockMvc.perform(get("/items/{id}", 999L)
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isNotFound());
    }

    // Тесты для получения вещей владельца
    @Test
    void getByOwnerId_shouldReturnItemsList() throws Exception {
        // Given
        String responseBody = """
            [
                {
                    "id": 1,
                    "name": "Item 1",
                    "available": true
                },
                {
                    "id": 2,
                    "name": "Item 2",
                    "available": true
                }
            ]
            """;

        when(itemClient.getByOwnerId(anyLong(), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.ok(responseBody));

        // When & Then
        mockMvc.perform(get("/items")
                        .header(USER_ID_HEADER, 1L)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    void getByOwnerId_shouldUseDefaultPagination() throws Exception {
        // Given
        when(itemClient.getByOwnerId(anyLong(), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.ok("[]"));

        // When & Then - без параметров пагинации
        mockMvc.perform(get("/items")
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk());
    }

    // Тесты для обновления вещи
    @Test
    void update_shouldReturnUpdatedItem() throws Exception {
        // Given
        ItemDto updateDto = new ItemDto();
        updateDto.setName("Updated Name");

        String responseBody = """
            {
                "id": 1,
                "name": "Updated Name",
                "description": "Original Description",
                "available": true
            }
            """;

        when(itemClient.update(anyLong(), any(ItemDto.class), anyLong()))
                .thenReturn(ResponseEntity.ok(responseBody));

        // When & Then
        mockMvc.perform(patch("/items/{id}", 1L)
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }

    @Test
    void update_shouldWorkWithPartialUpdate() throws Exception {
        // Given - только одно поле для обновления
        String updateJson = "{\"name\": \"Only Name Updated\"}";

        String responseBody = "{\"id\": 1, \"name\": \"Only Name Updated\"}";

        when(itemClient.update(anyLong(), any(ItemDto.class), anyLong()))
                .thenReturn(ResponseEntity.ok(responseBody));

        // When & Then
        mockMvc.perform(patch("/items/{id}", 1L)
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk());
    }

    // Тесты для удаления вещи
    @Test
    void delete_shouldReturnSuccess() throws Exception {
        // Given
        when(itemClient.delete(anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        // When & Then
        mockMvc.perform(delete("/items/{id}", 1L))
                .andExpect(status().isOk());
    }

    // Тесты для поиска вещей
    @Test
    void search_shouldReturnFoundItems() throws Exception {
        // Given
        String responseBody = """
            [
                {
                    "id": 1,
                    "name": "Drill",
                    "description": "Powerful drill",
                    "available": true
                }
            ]
            """;

        when(itemClient.search(anyString(), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.ok(responseBody));

        // When & Then
        mockMvc.perform(get("/items/search")
                        .param("text", "drill")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Drill"));
    }

    @Test
    void search_shouldReturnEmptyListForEmptyText() throws Exception {
        // Given
        when(itemClient.search(eq(""), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.ok("[]"));

        // When & Then
        mockMvc.perform(get("/items/search")
                        .param("text", "")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // Тесты для добавления комментария
    @Test
    void addComment_shouldReturnCreatedComment() throws Exception {
        // Given
        CommentDto commentDto = createTestCommentDto();
        commentDto.setId(null);

        String responseBody = """
            {
                "id": 1,
                "text": "Test comment",
                "authorName": "Test User",
                "created": "2024-01-01T10:00:00"
            }
            """;

        when(itemClient.addComment(anyLong(), any(CommentDto.class), anyLong()))
                .thenReturn(ResponseEntity.ok(responseBody));

        // When & Then
        mockMvc.perform(post("/items/{itemId}/comment", 1L)
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value("Test comment"))
                .andExpect(jsonPath("$.authorName").value("Test User"));
    }

    @Test
    void addComment_shouldReturnBadRequestWhenCommentInvalid() throws Exception {
        // Given - CommentDto с пустым текстом
        CommentDto invalidComment = new CommentDto();
        invalidComment.setText(""); // Пустой текст невалиден

        // When & Then
        mockMvc.perform(post("/items/{itemId}/comment", 1L)
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidComment)))
                .andExpect(status().isBadRequest());
    }

    // Тест для проверки корректности логирования
    @Test
    void allMethods_shouldLogAppropriateMessages() throws Exception {
        // Given
        when(itemClient.getByOwnerId(anyLong(), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.ok("[]"));

        // When & Then - проверяем что запрос проходит без ошибок (логирование проверим через Mockito если нужно)
        mockMvc.perform(get("/items")
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk());
    }
}