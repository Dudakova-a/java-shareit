package ru.practicum.shareit.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Map;

@Service
public class UserClient extends BaseClient {
    private static final String API_PREFIX = "/users";

    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .build()
        );
    }

    public ResponseEntity<Object> existsByEmail(String email) {
        Map<String, Object> parameters = Map.of("email", email);
        return get("/check-email?email={email}", null, parameters);
    }

    public ResponseEntity<Object> createUser(UserDto userDto) {
        return post("", userDto);  // Используем метод без userId
    }

    public ResponseEntity<Object> getUserById(Long id) {
        return get("/" + id);  // Используем метод без userId
    }

    public ResponseEntity<Object> getAllUsers(Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("?from={from}&size={size}", null, parameters);
    }

    public ResponseEntity<Object> updateUser(Long id, UserDto userDto) {
        return patch("/" + id, userDto);  // Используем patch(String path, T body)
    }

    public ResponseEntity<Object> deleteUser(Long id) {
        return delete("/" + id);  // Используем метод без userId
    }
}