package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.client.ItemClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * REST контроллер для работы с вещами (Gateway).
 * Использует кастомный заголовок X-Sharer-User-Id для идентификации пользователя.
 */
@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemClient itemClient;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody ItemDto itemDto,
                                         @RequestHeader(USER_ID_HEADER) Long ownerId) {
        log.info("POST /items - Creating item: {}, ownerId: {}", itemDto, ownerId);
        ResponseEntity<Object> response = itemClient.create(itemDto, ownerId);
        log.info("POST /items - Item creation completed with status: {}", response.getStatusCode());
        return response;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable Long id,
                                          @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("GET /items/{} - Getting item by id, userId: {}", id, userId);
        ResponseEntity<Object> response = itemClient.getById(id, userId);
        log.info("GET /items/{} - Item retrieval completed with status: {}", id, response.getStatusCode());
        return response;
    }

    @GetMapping
    public ResponseEntity<Object> getByOwnerId(@RequestHeader(USER_ID_HEADER) Long ownerId,
                                               @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                               @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("GET /items - Getting items for ownerId: {}, from: {}, size: {}", ownerId, from, size);
        ResponseEntity<Object> response = itemClient.getByOwnerId(ownerId, from, size);
        log.info("GET /items - Found items for ownerId: {}, response status: {}", ownerId, response.getStatusCode());
        return response;
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Long id,
                                         @RequestBody ItemDto itemDto,
                                         @RequestHeader(USER_ID_HEADER) Long ownerId) {
        log.info("PATCH /items/{} - Updating item: {}, ownerId: {}", id, itemDto, ownerId);
        ResponseEntity<Object> response = itemClient.update(id, itemDto, ownerId);
        log.info("PATCH /items/{} - Item update completed with status: {}", id, response.getStatusCode());
        return response;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        log.info("DELETE /items/{} - Deleting item", id);
        ResponseEntity<Object> response = itemClient.delete(id);
        log.info("DELETE /items/{} - Item deletion completed with status: {}", id, response.getStatusCode());
        return response;
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String text,
                                         @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                         @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("GET /items/search - Searching items with text: '{}', from: {}, size: {}", text, from, size);
        ResponseEntity<Object> response = itemClient.search(text, from, size);
        log.info("GET /items/search - Search completed with status: {}, text: '{}'", response.getStatusCode(), text);
        return response;
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@PathVariable Long itemId,
                                             @Valid @RequestBody CommentDto commentDto,
                                             @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("POST /items/{}/comment - Adding comment: {}, userId: {}", itemId, commentDto, userId);
        ResponseEntity<Object> response = itemClient.addComment(itemId, commentDto, userId);
        log.info("POST /items/{}/comment - Comment addition completed with status: {}", itemId, response.getStatusCode());
        return response;
    }
}