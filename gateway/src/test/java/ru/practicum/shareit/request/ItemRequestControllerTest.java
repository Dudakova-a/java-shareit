package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.client.ItemRequestClient;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestClient itemRequestClient;

    @Test
    void create_ShouldReturnResponseEntity() throws Exception {
        // Given
        Long userId = 1L;
        ItemRequestCreateDto createDto = new ItemRequestCreateDto();
        createDto.setDescription("Need a power drill");

        ItemRequestDto responseBody = new ItemRequestDto();
        responseBody.setId(1L);
        responseBody.setDescription("Need a power drill");
        responseBody.setCreated(LocalDateTime.now());
        responseBody.setItems(List.of());

        ResponseEntity<Object> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(itemRequestClient.create(any(ItemRequestCreateDto.class), eq(userId)))
                .thenReturn(responseEntity);

        // When & Then
        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Need a power drill"))
                .andExpect(jsonPath("$.items").isArray());
    }

    @Test
    void getByRequestorId_ShouldReturnResponseEntity() throws Exception {
        // Given
        Long userId = 1L;

        ItemRequestDto request1 = new ItemRequestDto();
        request1.setId(1L);
        request1.setDescription("First request");
        request1.setCreated(LocalDateTime.now().minusDays(1));
        request1.setItems(List.of());

        List<Object> responseBody = List.of(request1);
        ResponseEntity<Object> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(itemRequestClient.getByUserId(userId))
                .thenReturn(responseEntity);

        // When & Then
        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("First request"));
    }

    @Test
    void getAllExceptUser_ShouldReturnResponseEntity() throws Exception {
        // Given
        Long userId = 1L;
        int from = 0;
        int size = 10;

        ItemRequestDto otherRequest = new ItemRequestDto();
        otherRequest.setId(3L);
        otherRequest.setDescription("Other user request");
        otherRequest.setCreated(LocalDateTime.now());
        otherRequest.setItems(List.of());

        List<Object> responseBody = List.of(otherRequest);
        ResponseEntity<Object> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(itemRequestClient.getAllExceptUser(eq(userId), eq(from), eq(size)))
                .thenReturn(responseEntity);

        // When & Then
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(3L))
                .andExpect(jsonPath("$[0].description").value("Other user request"));
    }

    @Test
    void getAllExceptUser_WithDefaultPagination_ShouldReturnResponseEntity() throws Exception {
        // Given
        Long userId = 1L;

        ItemRequestDto request = new ItemRequestDto();
        request.setId(1L);
        request.setDescription("Default pagination request");
        request.setItems(List.of());

        List<Object> responseBody = List.of(request);
        ResponseEntity<Object> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(itemRequestClient.getAllExceptUser(eq(userId), eq(0), eq(10)))
                .thenReturn(responseEntity);

        // When & Then
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L));
    }


    @Test
    void getById_ShouldReturnResponseEntity() throws Exception {
        // Given
        Long requestId = 1L;
        Long userId = 1L;

        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setId(requestId);
        requestDto.setDescription("Specific request");
        requestDto.setCreated(LocalDateTime.now());
        requestDto.setItems(List.of());

        ResponseEntity<Object> responseEntity = new ResponseEntity<>(requestDto, HttpStatus.OK);

        when(itemRequestClient.getById(eq(requestId), eq(userId)))
                .thenReturn(responseEntity);

        // When & Then
        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestId))
                .andExpect(jsonPath("$.description").value("Specific request"));
    }

    @Test
    void update_ShouldReturnResponseEntity() throws Exception {
        // Given
        Long requestId = 1L;
        Long userId = 1L;
        ItemRequestDto updateDto = new ItemRequestDto();
        updateDto.setDescription("Updated description");

        ItemRequestDto updatedDto = new ItemRequestDto();
        updatedDto.setId(requestId);
        updatedDto.setDescription("Updated description");
        updatedDto.setItems(List.of());

        ResponseEntity<Object> responseEntity = new ResponseEntity<>(updatedDto, HttpStatus.OK);

        when(itemRequestClient.update(eq(requestId), any(ItemRequestDto.class), eq(userId)))
                .thenReturn(responseEntity);

        // When & Then
        mockMvc.perform(patch("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestId))
                .andExpect(jsonPath("$.description").value("Updated description"));
    }

    @Test
    void delete_ShouldReturnResponseEntity() throws Exception {
        // Given
        Long requestId = 1L;
        Long userId = 1L;

        ResponseEntity<Object> responseEntity = new ResponseEntity<>(HttpStatus.NO_CONTENT);

        when(itemRequestClient.delete(eq(requestId), eq(userId)))
                .thenReturn(responseEntity);

        // When & Then
        mockMvc.perform(delete("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isNoContent());
    }

    @Test
    void create_WithoutUserIdHeader_ShouldReturnBadRequest() throws Exception {
        // Given
        ItemRequestCreateDto createDto = new ItemRequestCreateDto();
        createDto.setDescription("Need a power drill");

        // When & Then
        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_WithInvalidRequestBody_ShouldReturnBadRequest() throws Exception {
        // Given
        Long userId = 1L;
        // Empty description should fail @Valid validation
        ItemRequestCreateDto createDto = new ItemRequestCreateDto();
        createDto.setDescription(""); // Invalid - empty

        // When & Then
        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getByRequestorId_WithoutUserIdHeader_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/requests"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllExceptUser_WithoutUserIdHeader_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/requests/all")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getById_WithoutUserIdHeader_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/requests/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_WithoutUserIdHeader_ShouldReturnBadRequest() throws Exception {
        // Given
        Long requestId = 1L;
        ItemRequestDto updateDto = new ItemRequestDto();
        updateDto.setDescription("Updated description");

        // When & Then
        mockMvc.perform(patch("/requests/{requestId}", requestId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void delete_WithoutUserIdHeader_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(delete("/requests/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_WithClientError_ShouldReturnErrorResponse() throws Exception {
        // Given
        Long userId = 1L;
        ItemRequestCreateDto createDto = new ItemRequestCreateDto();
        createDto.setDescription("Need a power drill");

        ResponseEntity<Object> errorResponse = new ResponseEntity<>(
                "Error message", HttpStatus.BAD_REQUEST);

        when(itemRequestClient.create(any(ItemRequestCreateDto.class), eq(userId)))
                .thenReturn(errorResponse);

        // When & Then
        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error message"));
    }
}