package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
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
     * @param itemRequestDto данные запроса из тела запроса.
     * @param requestorId    идентификатор пользователя из заголовка X-Sharer-User-Id.
     * @return созданный запрос.
     */
    @PostMapping
    public ItemRequestDto create(@RequestBody ItemRequestDto itemRequestDto,
                                 @RequestHeader(USER_ID_HEADER) Long requestorId) {
        return itemRequestService.create(itemRequestDto, requestorId);
    }

    /**
     * Возвращает все запросы текущего пользователя.
     * Запросы возвращаются в порядке от новых к старым.
     *
     * @param requestorId идентификатор пользователя из заголовка.
     * @return список запросов пользователя.
     */
    @GetMapping
    public List<ItemRequestDto> getByRequestorId(@RequestHeader(USER_ID_HEADER) Long requestorId) {
        return itemRequestService.getByRequestorId(requestorId);
    }

    /**
     * Возвращает все запросы других пользователей.
     * Запросы возвращаются в порядке от новых к старым.
     *
     * @param userId идентификатор пользователя из заголовка.
     * @return список запросов других пользователей.
     */
    @GetMapping("/all")
    public List<ItemRequestDto> getAllExceptRequestor(@RequestHeader(USER_ID_HEADER) Long userId) {
        return itemRequestService.getAllExceptRequestor(userId);
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
    public ItemRequestDto update(@PathVariable Long requestId,
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
