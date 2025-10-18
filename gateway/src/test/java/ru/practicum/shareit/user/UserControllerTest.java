package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.client.UserClient;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserClient userClient;


    @Test
    void getUserById_ShouldReturnUser() throws Exception {
        // Given
        Long userId = 1L;

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("John");
        userDto.setEmail("john@mail.com");

        ResponseEntity<Object> responseEntity = new ResponseEntity<>(userDto, HttpStatus.OK);

        when(userClient.getUserById(userId))
                .thenReturn(responseEntity);

        // When & Then
        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.email").value("john@mail.com"));
    }

    @Test
    void getAllUsers_ShouldReturnUserList() throws Exception {
        // Given
        UserDto user1 = new UserDto();
        user1.setId(1L);
        user1.setName("John");
        user1.setEmail("john@mail.com");

        UserDto user2 = new UserDto();
        user2.setId(2L);
        user2.setName("Alice");
        user2.setEmail("alice@mail.com");

        List<UserDto> users = List.of(user1, user2);
        ResponseEntity<Object> responseEntity = new ResponseEntity<>(users, HttpStatus.OK);

        // Исправлено: добавлены параметры пагинации
        when(userClient.getAllUsers(anyInt(), anyInt()))
                .thenReturn(responseEntity);

        // When & Then
        mockMvc.perform(get("/users")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("John"))
                .andExpect(jsonPath("$[0].email").value("john@mail.com"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Alice"))
                .andExpect(jsonPath("$[1].email").value("alice@mail.com"));
    }

    @Test
    void getAllUsers_WithDefaultPagination_ShouldReturnUserList() throws Exception {
        // Given
        UserDto user1 = new UserDto();
        user1.setId(1L);
        user1.setName("John");
        user1.setEmail("john@mail.com");

        List<UserDto> users = List.of(user1);
        ResponseEntity<Object> responseEntity = new ResponseEntity<>(users, HttpStatus.OK);

        // Исправлено: проверяем вызов с дефолтными значениями
        when(userClient.getAllUsers(0, 10))
                .thenReturn(responseEntity);

        // When & Then
        mockMvc.perform(get("/users")) // Без параметров - должны использоваться дефолтные
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("John"));
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser() throws Exception {
        // Given
        Long userId = 1L;
        UserDto updateDto = new UserDto();
        updateDto.setName("John Updated");
        updateDto.setEmail("john.updated@mail.com");

        UserDto updatedUser = new UserDto();
        updatedUser.setId(1L);
        updatedUser.setName("John Updated");
        updatedUser.setEmail("john.updated@mail.com");

        ResponseEntity<Object> responseEntity = new ResponseEntity<>(updatedUser, HttpStatus.OK);

        when(userClient.updateUser(eq(userId), any(UserDto.class)))
                .thenReturn(responseEntity);

        // When & Then
        mockMvc.perform(patch("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John Updated"))
                .andExpect(jsonPath("$.email").value("john.updated@mail.com"));
    }

    @Test
    void deleteUser_ShouldReturnNoContent() throws Exception {
        // Given
        Long userId = 1L;

        ResponseEntity<Object> responseEntity = new ResponseEntity<>(HttpStatus.NO_CONTENT);

        when(userClient.deleteUser(userId))
                .thenReturn(responseEntity);

        // When & Then
        mockMvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isNoContent());
    }


    @Test
    void updateUser_WithDuplicateEmail_ShouldReturnConflict() throws Exception {
        // Given
        Long userId = 1L;
        UserDto updateDto = new UserDto();
        updateDto.setEmail("duplicate@mail.com");

        ResponseEntity<Object> responseEntity = new ResponseEntity<>(
                "Email already exists", HttpStatus.CONFLICT);

        when(userClient.updateUser(eq(userId), any(UserDto.class)))
                .thenReturn(responseEntity);

        // When & Then
        mockMvc.perform(patch("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Email already exists"));
    }

    @Test
    void getUserById_WhenUserNotFound_ShouldReturnNotFound() throws Exception {
        // Given
        Long userId = 999L;

        ResponseEntity<Object> responseEntity = new ResponseEntity<>(
                "User not found", HttpStatus.NOT_FOUND);

        when(userClient.getUserById(userId))
                .thenReturn(responseEntity);

        // When & Then
        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found"));
    }

    @Test
    void deleteUser_WhenUserNotFound_ShouldReturnNotFound() throws Exception {
        // Given
        Long userId = 999L;

        ResponseEntity<Object> responseEntity = new ResponseEntity<>(
                "User not found", HttpStatus.NOT_FOUND);

        when(userClient.deleteUser(userId))
                .thenReturn(responseEntity);

        // When & Then
        mockMvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found"));
    }

    @Test
    void existsByEmail_WithoutEmailParam_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/users/check-email"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUser_WithPartialUpdate_ShouldReturnUpdatedUser() throws Exception {
        // Given
        Long userId = 1L;
        UserDto updateDto = new UserDto();
        updateDto.setName("John Updated Only Name"); // Only update name, keep email

        UserDto updatedUser = new UserDto();
        updatedUser.setId(1L);
        updatedUser.setName("John Updated Only Name");
        updatedUser.setEmail("original@mail.com"); // Email remains unchanged

        ResponseEntity<Object> responseEntity = new ResponseEntity<>(updatedUser, HttpStatus.OK);

        when(userClient.updateUser(eq(userId), any(UserDto.class)))
                .thenReturn(responseEntity);

        // When & Then
        mockMvc.perform(patch("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John Updated Only Name"))
                .andExpect(jsonPath("$.email").value("original@mail.com"));
    }

    @Test
    void getAllUsers_WhenEmpty_ShouldReturnEmptyList() throws Exception {
        // Given
        List<UserDto> emptyList = List.of();
        ResponseEntity<Object> responseEntity = new ResponseEntity<>(emptyList, HttpStatus.OK);

        when(userClient.getAllUsers(anyInt(), anyInt()))
                .thenReturn(responseEntity);

        // When & Then
        mockMvc.perform(get("/users")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getAllUsers_WithCustomPagination_ShouldReturnUserList() throws Exception {
        // Given
        UserDto user1 = new UserDto();
        user1.setId(1L);
        user1.setName("John");
        user1.setEmail("john@mail.com");

        List<UserDto> users = List.of(user1);
        ResponseEntity<Object> responseEntity = new ResponseEntity<>(users, HttpStatus.OK);

        // Проверяем с кастомными параметрами пагинации
        when(userClient.getAllUsers(5, 20))
                .thenReturn(responseEntity);

        // When & Then
        mockMvc.perform(get("/users")
                        .param("from", "5")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L));
    }
}