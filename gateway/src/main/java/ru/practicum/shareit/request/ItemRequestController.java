package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.client.ItemRequestClient;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * REST контроллер для работы с запросами вещей (Gateway).
 */
@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody ItemRequestCreateDto itemRequestCreateDto,
                                         @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("POST /requests - Creating item request: {}, userId: {}", itemRequestCreateDto, userId);
        ResponseEntity<Object> response = itemRequestClient.create(itemRequestCreateDto, userId);
        log.info("POST /requests - Item request creation completed with status: {}", response.getStatusCode());
        return response;
    }

    @GetMapping
    public ResponseEntity<Object> getByRequestorId(@RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("GET /requests - Getting item requests for userId: {}", userId);
        ResponseEntity<Object> response = itemRequestClient.getByUserId(userId);
        log.info("GET /requests - Found item requests for userId: {}, response status: {}",
                userId, response.getStatusCode());
        return response;
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllExceptUser(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
            @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("GET /requests/all - Getting all item requests except userId: {}, from: {}, size: {}",
                userId, from, size);
        ResponseEntity<Object> response = itemRequestClient.getAllExceptUser(userId, from, size);
        log.info("GET /requests/all - Found item requests for others, response status: {}", response.getStatusCode());
        return response;
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@PathVariable Long requestId,
                                          @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("GET /requests/{} - Getting item request by id, userId: {}", requestId, userId);
        ResponseEntity<Object> response = itemRequestClient.getById(requestId, userId);
        log.info("GET /requests/{} - Item request retrieval completed with status: {}",
                requestId, response.getStatusCode());
        return response;
    }

    @PatchMapping("/{requestId}")
    public ResponseEntity<Object> update(@PathVariable Long requestId,
                                         @RequestBody ItemRequestDto itemRequestDto,
                                         @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("PATCH /requests/{} - Updating item request: {}, userId: {}", requestId, itemRequestDto, userId);
        ResponseEntity<Object> response = itemRequestClient.update(requestId, itemRequestDto, userId);
        log.info("PATCH /requests/{} - Item request update completed with status: {}",
                requestId, response.getStatusCode());
        return response;
    }

    @DeleteMapping("/{requestId}")
    public ResponseEntity<Object> delete(@PathVariable Long requestId,
                                         @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("DELETE /requests/{} - Deleting item request, userId: {}", requestId, userId);
        ResponseEntity<Object> response = itemRequestClient.delete(requestId, userId);
        log.info("DELETE /requests/{} - Item request deletion completed with status: {}",
                requestId, response.getStatusCode());
        return response;
    }
}