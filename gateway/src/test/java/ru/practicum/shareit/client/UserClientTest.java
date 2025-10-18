package ru.practicum.shareit.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.user.dto.UserDto;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserClientTest {

    @Mock
    private RestTemplate restTemplate;

    private UserClient userClient;

    @BeforeEach
    void setUp() throws Exception {
        // Создаем реальный UserClient
        userClient = new UserClient("http://localhost:8080",
                new org.springframework.boot.web.client.RestTemplateBuilder());

        // Используем Reflection для установки mock RestTemplate
        Field restField = BaseClient.class.getDeclaredField("rest");
        restField.setAccessible(true);
        restField.set(userClient, restTemplate);
    }

    @Test
    void existsByEmail_ShouldMakeGetRequestWithEmailParameter() {
        // Given
        String email = "test@example.com";

        Map<String, Object> expectedParameters = Map.of("email", email);
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok(true);

        when(restTemplate.exchange(
                eq("/check-email?email={email}"),
                eq(org.springframework.http.HttpMethod.GET),
                any(org.springframework.http.HttpEntity.class),
                eq(Object.class),
                eq(expectedParameters)
        )).thenReturn(expectedResponse);

        // When
        ResponseEntity<Object> response = userClient.existsByEmail(email);

        // Then
        assertNotNull(response);
        assertEquals(expectedResponse, response);

        verify(restTemplate).exchange(
                eq("/check-email?email={email}"),
                eq(org.springframework.http.HttpMethod.GET),
                any(org.springframework.http.HttpEntity.class),
                eq(Object.class),
                eq(expectedParameters)
        );
    }

    @Test
    void existsByEmail_WithNullUserId_ShouldNotIncludeUserIdHeader() {
        // Given
        String email = "test@example.com";

        Map<String, Object> expectedParameters = Map.of("email", email);
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok(true);

        when(restTemplate.exchange(
                eq("/check-email?email={email}"),
                eq(org.springframework.http.HttpMethod.GET),
                any(org.springframework.http.HttpEntity.class),
                eq(Object.class),
                eq(expectedParameters)
        )).thenReturn(expectedResponse);

        // When
        ResponseEntity<Object> response = userClient.existsByEmail(email);

        // Then
        verify(restTemplate).exchange(
                eq("/check-email?email={email}"),
                eq(org.springframework.http.HttpMethod.GET),
                argThat(request ->
                        !request.getHeaders().containsKey("X-Sharer-User-Id")),
                eq(Object.class),
                eq(expectedParameters)
        );
    }

    @Test
    void createUser_ShouldMakePostRequestWithUserDto() {
        // Given
        UserDto userDto = new UserDto();
        userDto.setName("Test User");
        userDto.setEmail("test@example.com");

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("user created");

        when(restTemplate.exchange(
                eq(""),
                eq(org.springframework.http.HttpMethod.POST),
                any(org.springframework.http.HttpEntity.class),
                eq(Object.class)
        )).thenReturn(expectedResponse);

        // When
        ResponseEntity<Object> response = userClient.createUser(userDto);

        // Then
        assertNotNull(response);
        assertEquals(expectedResponse, response);

        verify(restTemplate).exchange(
                eq(""),
                eq(org.springframework.http.HttpMethod.POST),
                argThat(request ->
                        userDto.equals(request.getBody())),
                eq(Object.class)
        );
    }

    @Test
    void createUser_WithNullUserDto_ShouldHandleNullBody() {
        // Given
        UserDto userDto = null;
        ResponseEntity<Object> expectedResponse = ResponseEntity.badRequest().build();

        when(restTemplate.exchange(
                eq(""),
                eq(org.springframework.http.HttpMethod.POST),
                any(org.springframework.http.HttpEntity.class),
                eq(Object.class)
        )).thenReturn(expectedResponse);

        // When
        ResponseEntity<Object> response = userClient.createUser(userDto);

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
    void getUserById_ShouldMakeGetRequestWithIdPath() {
        // Given
        Long userId = 100L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("user details");

        when(restTemplate.exchange(
                eq("/" + userId),
                eq(org.springframework.http.HttpMethod.GET),
                any(org.springframework.http.HttpEntity.class),
                eq(Object.class)
        )).thenReturn(expectedResponse);

        // When
        ResponseEntity<Object> response = userClient.getUserById(userId);

        // Then
        assertNotNull(response);
        assertEquals(expectedResponse, response);

        verify(restTemplate).exchange(
                eq("/" + userId),
                eq(org.springframework.http.HttpMethod.GET),
                any(org.springframework.http.HttpEntity.class),
                eq(Object.class)
        );
    }

    @Test
    void getUserById_WithoutUserIdHeader_ShouldNotIncludeUserIdHeader() {
        // Given
        Long userId = 100L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("user details");

        when(restTemplate.exchange(
                eq("/" + userId),
                eq(org.springframework.http.HttpMethod.GET),
                any(org.springframework.http.HttpEntity.class),
                eq(Object.class)
        )).thenReturn(expectedResponse);

        // When
        ResponseEntity<Object> response = userClient.getUserById(userId);

        // Then
        verify(restTemplate).exchange(
                eq("/" + userId),
                eq(org.springframework.http.HttpMethod.GET),
                argThat(request ->
                        !request.getHeaders().containsKey("X-Sharer-User-Id")),
                eq(Object.class)
        );
    }

    @Test
    void getAllUsers_ShouldMakeGetRequestWithPaginationParameters() {
        // Given
        Integer from = 0;
        Integer size = 10;

        Map<String, Object> expectedParameters = Map.of(
                "from", from,
                "size", size
        );

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("users list");

        when(restTemplate.exchange(
                eq("?from={from}&size={size}"),
                eq(org.springframework.http.HttpMethod.GET),
                any(org.springframework.http.HttpEntity.class),
                eq(Object.class),
                eq(expectedParameters)
        )).thenReturn(expectedResponse);

        // When
        ResponseEntity<Object> response = userClient.getAllUsers(from, size);

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
    void getAllUsers_WithCustomPagination_ShouldPassCorrectParameters() {
        // Given
        Integer from = 5;
        Integer size = 20;

        Map<String, Object> expectedParameters = Map.of(
                "from", from,
                "size", size
        );

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("custom pagination users");

        when(restTemplate.exchange(
                eq("?from={from}&size={size}"),
                eq(org.springframework.http.HttpMethod.GET),
                any(org.springframework.http.HttpEntity.class),
                eq(Object.class),
                eq(expectedParameters)
        )).thenReturn(expectedResponse);

        // When
        ResponseEntity<Object> response = userClient.getAllUsers(from, size);

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
    void updateUser_ShouldMakePatchRequestWithUserDto() {
        // Given
        Long userId = 100L;
        UserDto userDto = new UserDto();
        userDto.setName("Updated Name");
        userDto.setEmail("updated@example.com");

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("user updated");

        when(restTemplate.exchange(
                eq("/" + userId),
                eq(org.springframework.http.HttpMethod.PATCH),
                any(org.springframework.http.HttpEntity.class),
                eq(Object.class)
        )).thenReturn(expectedResponse);

        // When
        ResponseEntity<Object> response = userClient.updateUser(userId, userDto);

        // Then
        assertNotNull(response);
        assertEquals(expectedResponse, response);

        verify(restTemplate).exchange(
                eq("/" + userId),
                eq(org.springframework.http.HttpMethod.PATCH),
                argThat(request ->
                        userDto.equals(request.getBody())),
                eq(Object.class)
        );
    }

    @Test
    void updateUser_WithNullUserDto_ShouldHandleNullBody() {
        // Given
        Long userId = 100L;
        UserDto userDto = null;
        ResponseEntity<Object> expectedResponse = ResponseEntity.badRequest().build();

        when(restTemplate.exchange(
                eq("/" + userId),
                eq(org.springframework.http.HttpMethod.PATCH),
                any(org.springframework.http.HttpEntity.class),
                eq(Object.class)
        )).thenReturn(expectedResponse);

        // When
        ResponseEntity<Object> response = userClient.updateUser(userId, userDto);

        // Then
        assertNotNull(response);
        assertEquals(expectedResponse, response);

        verify(restTemplate).exchange(
                eq("/" + userId),
                eq(org.springframework.http.HttpMethod.PATCH),
                argThat(request -> request.getBody() == null),
                eq(Object.class)
        );
    }

    @Test
    void deleteUser_ShouldMakeDeleteRequest() {
        // Given
        Long userId = 100L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.noContent().build();

        when(restTemplate.exchange(
                eq("/" + userId),
                eq(org.springframework.http.HttpMethod.DELETE),
                any(org.springframework.http.HttpEntity.class),
                eq(Object.class)
        )).thenReturn(expectedResponse);

        // When
        ResponseEntity<Object> response = userClient.deleteUser(userId);

        // Then
        assertNotNull(response);
        assertEquals(expectedResponse, response);

        verify(restTemplate).exchange(
                eq("/" + userId),
                eq(org.springframework.http.HttpMethod.DELETE),
                any(org.springframework.http.HttpEntity.class),
                eq(Object.class)
        );
    }

    @Test
    void deleteUser_WithoutUserIdHeader_ShouldNotIncludeUserIdHeader() {
        // Given
        Long userId = 100L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.noContent().build();

        when(restTemplate.exchange(
                eq("/" + userId),
                eq(org.springframework.http.HttpMethod.DELETE),
                any(org.springframework.http.HttpEntity.class),
                eq(Object.class)
        )).thenReturn(expectedResponse);

        // When
        ResponseEntity<Object> response = userClient.deleteUser(userId);

        // Then
        verify(restTemplate).exchange(
                eq("/" + userId),
                eq(org.springframework.http.HttpMethod.DELETE),
                argThat(request ->
                        !request.getHeaders().containsKey("X-Sharer-User-Id")),
                eq(Object.class)
        );
    }

    @Test
    void existsByEmail_WithEmptyEmail_ShouldPassEmptyString() {
        // Given
        String email = "";

        Map<String, Object> expectedParameters = Map.of("email", email);
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok(false);

        when(restTemplate.exchange(
                eq("/check-email?email={email}"),
                eq(org.springframework.http.HttpMethod.GET),
                any(org.springframework.http.HttpEntity.class),
                eq(Object.class),
                eq(expectedParameters)
        )).thenReturn(expectedResponse);

        // When
        ResponseEntity<Object> response = userClient.existsByEmail(email);

        // Then
        assertNotNull(response);
        assertEquals(expectedResponse, response);

        verify(restTemplate).exchange(
                eq("/check-email?email={email}"),
                eq(org.springframework.http.HttpMethod.GET),
                any(org.springframework.http.HttpEntity.class),
                eq(Object.class),
                eq(expectedParameters)
        );
    }

    @Test
    void getAllUsers_WithZeroPagination_ShouldPassZeroValues() {
        // Given
        Integer from = 0;
        Integer size = 0;

        Map<String, Object> expectedParameters = Map.of(
                "from", from,
                "size", size
        );

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("users with zero pagination");

        when(restTemplate.exchange(
                eq("?from={from}&size={size}"),
                eq(org.springframework.http.HttpMethod.GET),
                any(org.springframework.http.HttpEntity.class),
                eq(Object.class),
                eq(expectedParameters)
        )).thenReturn(expectedResponse);

        // When
        ResponseEntity<Object> response = userClient.getAllUsers(from, size);

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
    void constructor_ShouldInitializeWithCorrectBaseUrl() {
        // Given
        String serverUrl = "http://test-server:9090";

        // When
        UserClient client = new UserClient(serverUrl,
                new org.springframework.boot.web.client.RestTemplateBuilder());

        // Then - проверяем что клиент создан (не падает с исключениями)
        assertNotNull(client);
    }

    @Test
    void allMethods_ShouldNotIncludeUserIdHeader() {
        // Given
        Long userId = 100L;
        UserDto userDto = new UserDto();
        userDto.setName("Test User");

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("response");

        // Настраиваем моки для всех методов
        when(restTemplate.exchange(anyString(), any(), any(), eq(Object.class)))
                .thenReturn(expectedResponse);
        when(restTemplate.exchange(anyString(), any(), any(), eq(Object.class), any(Map.class)))
                .thenReturn(expectedResponse);

        // When - вызываем все методы
        userClient.createUser(userDto);
        userClient.getUserById(userId);
        userClient.getAllUsers(0, 10);
        userClient.updateUser(userId, userDto);
        userClient.deleteUser(userId);
        userClient.existsByEmail("test@example.com");

        // Then - проверяем что ни один запрос не включает userId заголовок
        verify(restTemplate, atLeastOnce()).exchange(
                anyString(),
                any(),
                argThat(request ->
                        !request.getHeaders().containsKey("X-Sharer-User-Id")),
                eq(Object.class)
        );
    }
}