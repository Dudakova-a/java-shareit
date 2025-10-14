package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

/**
 * REST контроллер для работы с запросами вещей.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    // Внедрение зависимости сервиса через конструктор
    private final ItemRequestService itemRequestService;

    // Имя заголовка для передачи идентификатора пользователя
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    /**
     * Создает новый запрос на вещь.
     *
     * @param itemRequestCreateDto данные запроса из тела запроса.
     * @param userId               идентификатор пользователя из заголовка X-Sharer-User-Id.
     * @return созданный запрос.
     */
    @PostMapping
    public ItemRequestDto create(@Valid @RequestBody ItemRequestCreateDto itemRequestCreateDto,
                                 @RequestHeader(USER_ID_HEADER) Long userId) {
        return itemRequestService.create(itemRequestCreateDto, userId);
    }

    /**
     * Возвращает все запросы текущего пользователя.
     * Запросы возвращаются в порядке от новых к старым.
     *
     * @param userId идентификатор пользователя из заголовка.
     * @return список запросов пользователя.
     */
    @GetMapping
    public List<ItemRequestDto> getByRequestorId(@RequestHeader(USER_ID_HEADER) Long userId) {
        return itemRequestService.getByUserId(userId);
    }

    /**
     * Возвращает все запросы других пользователей.
     * Запросы возвращаются в порядке от новых к старым.
     *
     * @param userId идентификатор пользователя из заголовка.
     * @return список запросов других пользователей.
     */
    @GetMapping("/all")
    public List<ItemRequestDto> getAllExceptUser(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        return itemRequestService.getAllExceptUser(userId, from, size);
    }

    /**
     * Возвращает конкретный запрос по идентификатору.
     *
     * @param requestId идентификатор запроса.
     * @return запрос.
     */
    @GetMapping("/{requestId}")
    public ItemRequestDto getById(@PathVariable Long requestId) {
        return itemRequestService.getById(requestId);
    }

    /**
     * Обновляет данные запроса.
     *
     * @param requestId      идентификатор запроса
     * @param itemRequestDto данные для обновления
     * @return обновленный запрос
     */
    @PatchMapping("/{requestId}")
    public ItemRequestDto update(@Valid @PathVariable Long requestId,
                                 @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.update(requestId, itemRequestDto);
    }

    /**
     * Удаляет запрос.
     *
     * @param requestId идентификатор запроса для удаления.
     */
    @DeleteMapping("/{requestId}")
    public void delete(@PathVariable Long requestId) {
        itemRequestService.delete(requestId);
    }
}