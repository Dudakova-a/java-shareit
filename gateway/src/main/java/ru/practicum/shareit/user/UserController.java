package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.client.UserClient;
import ru.practicum.shareit.user.dto.UserDto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserDto userDto) {
        // Проверяем существование email
        ResponseEntity<Object> emailCheckResponse = userClient.existsByEmail(userDto.getEmail());

        // Если запрос успешен и email существует, возвращаем ошибку в формате JSON
        if (emailCheckResponse.getStatusCode().is2xxSuccessful() &&
                emailCheckResponse.getBody() instanceof Boolean &&
                (Boolean) emailCheckResponse.getBody()) {
            log.warn("Email already exists: {}", userDto.getEmail());

            // Возвращаем JSON вместо простого текста
            Map<String, String> errorResponse = Map.of("error", "Email already exists");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }

        log.info("POST /users - Creating user: {}", userDto);
        ResponseEntity<Object> response = userClient.createUser(userDto);
        log.info("POST /users - User creation completed with status: {}", response.getStatusCode());
        return response;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable Long id) {
        log.info("GET /users/{} - Getting user by id", id);
        ResponseEntity<Object> response = userClient.getUserById(id);
        log.info("GET /users/{} - User retrieval completed with status: {}", id, response.getStatusCode());
        return response;
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers(@PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                              @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("GET /users - Getting all users, from: {}, size: {}", from, size);
        ResponseEntity<Object> response = userClient.getAllUsers(from, size);
        log.info("GET /users - Users retrieval completed with status: {}", response.getStatusCode());
        return response;
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable Long id,
                                             @RequestBody UserDto userDto) {
        log.info("PATCH /users/{} - Updating user with data: {}", id, userDto);
        ResponseEntity<Object> response = userClient.updateUser(id, userDto);
        log.info("PATCH /users/{} - User update completed with status: {}", id, response.getStatusCode());
        return response;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long id) {
        log.info("DELETE /users/{} - Deleting user", id);
        ResponseEntity<Object> response = userClient.deleteUser(id);
        log.info("DELETE /users/{} - User deletion completed with status: {}", id, response.getStatusCode());
        return response;
    }
}