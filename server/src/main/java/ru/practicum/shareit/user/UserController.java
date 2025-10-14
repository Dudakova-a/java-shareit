package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import jakarta.validation.Valid;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        log.info("POST /users - Creating user: {}", userDto);
        UserDto result = userService.createUser(userDto);
        log.info("POST /users - User created successfully: {}", result);
        return result;
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable Long id) {
        log.info("GET /users/{} - Getting user by id", id);
        UserDto result = userService.getUserById(id);
        log.info("GET /users/{} - User found: {}", id, result);
        return result;
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("GET /users - Getting all users");
        List<UserDto> result = userService.getAllUsers();
        log.info("GET /users - Found {} users", result.size());
        return result;
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        log.info("PATCH /users/{} - Updating user with data: {}", id, userDto);
        UserDto result = userService.updateUser(id, userDto);
        log.info("PATCH /users/{} - User updated successfully: {}", id, result);
        return result;
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        log.info("DELETE /users/{} - Deleting user", id);
        userService.deleteUser(id);
        log.info("DELETE /users/{} - User deleted successfully", id);
    }
}