package ru.practicum.shareit.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BaseClientTest {

    @Mock
    private RestTemplate restTemplate;

    private BaseClient baseClient;

    @BeforeEach
    void setUp() {
        baseClient = new BaseClient(restTemplate);
    }

    @Test
    void get_WithoutUserIdAndParameters_ShouldMakeCorrectRequest() {
        // Given
        String path = "/test";
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("test response");

        when(restTemplate.exchange(eq(path), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        // When
        ResponseEntity<Object> response = baseClient.get(path);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("test response", response.getBody());

        verify(restTemplate).exchange(eq(path), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void get_WithUserId_ShouldIncludeUserIdHeader() {
        // Given
        String path = "/test";
        long userId = 123L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("response");

        when(restTemplate.exchange(eq(path), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        // When
        ResponseEntity<Object> response = baseClient.get(path, userId);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(restTemplate).exchange(eq(path), eq(HttpMethod.GET), argThat(request -> {
            HttpHeaders headers = request.getHeaders();
            return headers.containsKey("X-Sharer-User-Id") &&
                    "123".equals(headers.getFirst("X-Sharer-User-Id"));
        }), eq(Object.class));
    }

    @Test
    void get_WithParameters_ShouldPassParameters() {
        // Given
        String path = "/test/{id}";
        long userId = 123L;
        Map<String, Object> parameters = Map.of("id", 456L);
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("response");

        when(restTemplate.exchange(eq(path), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class), eq(parameters)))
                .thenReturn(expectedResponse);

        // When
        ResponseEntity<Object> response = baseClient.get(path, userId, parameters);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(restTemplate).exchange(eq(path), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class), eq(parameters));
    }

    @Test
    void post_WithBody_ShouldMakePostRequest() {
        // Given
        String path = "/test";
        String requestBody = "test body";
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("created");

        when(restTemplate.exchange(eq(path), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        // When
        ResponseEntity<Object> response = baseClient.post(path, requestBody);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("created", response.getBody());

        verify(restTemplate).exchange(eq(path), eq(HttpMethod.POST), argThat(request ->
                requestBody.equals(request.getBody())), eq(Object.class));
    }

    @Test
    void post_WithUserIdAndBody_ShouldIncludeHeadersAndBody() {
        // Given
        String path = "/test";
        long userId = 123L;
        TestBody requestBody = new TestBody("test data");
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("response");

        when(restTemplate.exchange(eq(path), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        // When
        ResponseEntity<Object> response = baseClient.post(path, userId, requestBody);

        // Then
        assertNotNull(response);

        verify(restTemplate).exchange(eq(path), eq(HttpMethod.POST), argThat(request -> {
            HttpHeaders headers = request.getHeaders();
            return requestBody.equals(request.getBody()) &&
                    headers.containsKey("X-Sharer-User-Id") &&
                    "123".equals(headers.getFirst("X-Sharer-User-Id")) &&
                    MediaType.APPLICATION_JSON.equals(headers.getContentType());
        }), eq(Object.class));
    }

    @Test
    void put_WithUserIdAndBody_ShouldMakePutRequest() {
        // Given
        String path = "/test";
        long userId = 123L;
        String requestBody = "update data";
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("updated");

        when(restTemplate.exchange(eq(path), eq(HttpMethod.PUT), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        // When
        ResponseEntity<Object> response = baseClient.put(path, userId, requestBody);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(restTemplate).exchange(eq(path), eq(HttpMethod.PUT), argThat(request ->
                requestBody.equals(request.getBody())), eq(Object.class));
    }

    @Test
    void patch_WithUserId_ShouldMakePatchRequest() {
        // Given
        String path = "/test";
        long userId = 123L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("patched");

        when(restTemplate.exchange(eq(path), eq(HttpMethod.PATCH), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        // When
        ResponseEntity<Object> response = baseClient.patch(path, userId);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(restTemplate).exchange(eq(path), eq(HttpMethod.PATCH), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void patch_WithUserIdAndBody_ShouldIncludeBody() {
        // Given
        String path = "/test";
        long userId = 123L;
        String requestBody = "patch data";
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("patched");

        when(restTemplate.exchange(eq(path), eq(HttpMethod.PATCH), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        // When
        ResponseEntity<Object> response = baseClient.patch(path, userId, requestBody);

        // Then
        assertNotNull(response);

        verify(restTemplate).exchange(eq(path), eq(HttpMethod.PATCH), argThat(request ->
                requestBody.equals(request.getBody())), eq(Object.class));
    }

    @Test
    void delete_WithUserId_ShouldMakeDeleteRequest() {
        // Given
        String path = "/test";
        long userId = 123L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.noContent().build();

        when(restTemplate.exchange(eq(path), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        // When
        ResponseEntity<Object> response = baseClient.delete(path, userId);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        verify(restTemplate).exchange(eq(path), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void whenHttpClientErrorException_ShouldReturnErrorResponse() {
        // Given
        String path = "/test";
        String errorBody = "Error message";
        HttpClientErrorException exception = HttpClientErrorException.create(
                HttpStatus.BAD_REQUEST, "Bad Request",
                HttpHeaders.EMPTY, errorBody.getBytes(), null);

        when(restTemplate.exchange(eq(path), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenThrow(exception);

        // When
        ResponseEntity<Object> response = baseClient.get(path);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertArrayEquals(errorBody.getBytes(), (byte[]) response.getBody());
    }

    @Test
    void whenHttpServerErrorException_ShouldReturnErrorResponse() {
        // Given
        String path = "/test";
        String errorBody = "Server error";
        HttpServerErrorException exception = HttpServerErrorException.create(
                HttpStatus.INTERNAL_SERVER_ERROR, "Server Error",
                HttpHeaders.EMPTY, errorBody.getBytes(), null);

        when(restTemplate.exchange(eq(path), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class)))
                .thenThrow(exception);

        // When
        ResponseEntity<Object> response = baseClient.post(path, "body");

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertArrayEquals(errorBody.getBytes(), (byte[]) response.getBody());
    }

    @Test
    void prepareGatewayResponse_With2xxResponse_ShouldReturnSameResponse() {
        // Given
        String path = "/test";
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");

        when(restTemplate.exchange(eq(path), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        // When
        ResponseEntity<Object> result = baseClient.get(path);

        // Then
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("success", result.getBody());
    }

    @Test
    void prepareGatewayResponse_With4xxResponse_ShouldReturnErrorResponse() {
        // Given
        String path = "/test";
        String errorBody = "Not found";
        HttpClientErrorException exception = HttpClientErrorException.create(
                HttpStatus.NOT_FOUND, "Not Found",
                HttpHeaders.EMPTY, errorBody.getBytes(), null);

        when(restTemplate.exchange(eq(path), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenThrow(exception);

        // When
        ResponseEntity<Object> result = baseClient.get(path);

        // Then
        assertNotNull(result);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertArrayEquals(errorBody.getBytes(), (byte[]) result.getBody());
    }

    @Test
    void prepareGatewayResponse_With5xxResponseAndBody_ShouldReturnResponseWithBody() {
        // Given
        String path = "/test";
        String errorBody = "Internal server error";
        HttpServerErrorException exception = HttpServerErrorException.create(
                HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
                HttpHeaders.EMPTY, errorBody.getBytes(), null);

        when(restTemplate.exchange(eq(path), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class)))
                .thenThrow(exception);

        // When
        ResponseEntity<Object> result = baseClient.post(path, "body");

        // Then
        assertNotNull(result);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertArrayEquals(errorBody.getBytes(), (byte[]) result.getBody());
    }


    @Test
    void defaultHeaders_WithUserId_ShouldSetCorrectHeaders() {
        // Given
        String path = "/test";
        long userId = 123L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("response");

        when(restTemplate.exchange(eq(path), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        // When
        ResponseEntity<Object> response = baseClient.get(path, userId);

        // Then
        verify(restTemplate).exchange(eq(path), eq(HttpMethod.GET), argThat(request -> {
            HttpHeaders headers = request.getHeaders();
            return headers.getContentType().equals(MediaType.APPLICATION_JSON) &&
                    headers.getAccept().contains(MediaType.APPLICATION_JSON) &&
                    headers.containsKey("X-Sharer-User-Id") &&
                    "123".equals(headers.getFirst("X-Sharer-User-Id"));
        }), eq(Object.class));
    }

    @Test
    void defaultHeaders_WithoutUserId_ShouldNotSetUserIdHeader() {
        // Given
        String path = "/test";
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("response");

        when(restTemplate.exchange(eq(path), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        // When
        ResponseEntity<Object> response = baseClient.get(path);

        // Then
        verify(restTemplate).exchange(eq(path), eq(HttpMethod.GET), argThat(request -> {
            HttpHeaders headers = request.getHeaders();
            return !headers.containsKey("X-Sharer-User-Id") &&
                    headers.getContentType().equals(MediaType.APPLICATION_JSON) &&
                    headers.getAccept().contains(MediaType.APPLICATION_JSON);
        }), eq(Object.class));
    }

    @Test
    void defaultHeaders_WithNullUserId_ShouldNotSetUserIdHeader() {
        // Given
        String path = "/test";
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("response");

        when(restTemplate.exchange(eq(path), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        // When
        ResponseEntity<Object> response = baseClient.get(path, (Long) null, null);

        // Then
        verify(restTemplate).exchange(eq(path), eq(HttpMethod.GET), argThat(request -> {
            HttpHeaders headers = request.getHeaders();
            return !headers.containsKey("X-Sharer-User-Id") &&
                    headers.getContentType().equals(MediaType.APPLICATION_JSON) &&
                    headers.getAccept().contains(MediaType.APPLICATION_JSON);
        }), eq(Object.class));
    }

    // Вспомогательный класс для тестирования
    private static class TestBody {
        private String data;

        public TestBody(String data) {
            this.data = data;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TestBody testBody = (TestBody) o;
            return data.equals(testBody.data);
        }

        @Override
        public int hashCode() {
            return data.hashCode();
        }
    }
}