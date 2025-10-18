package ru.practicum.shareit.request;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

/**
 * Интерфейс сервиса для работы с запросами вещей.
 */
public interface ItemRequestService {
    /**
     * Создает новый запрос от имени пользователя.
     *
     * @param itemRequestDto DTO с данными запроса.
     * @param requestorId    идентификатор пользователя, создающего запрос.
     * @return созданный запрос в формате DTO.
     */
    ItemRequestDto create(ItemRequestCreateDto itemRequestDto, Long requestorId);

    /**
     * Находит запрос по идентификатору.
     *
     * @param id идентификатор запроса.
     * @return запрос в формате DTO.
     * @throws NotFoundException если запрос не найден.
     */
    ItemRequestDto getById(Long id);

    /**
     * Возвращает все запросы текущего пользователя.
     *
     * @param requestorId идентификатор пользователя.
     * @return список запросов пользователя в формате DTO.
     */
    List<ItemRequestDto> getByUserId(Long requestorId);

    /**
     * Возвращает все запросы других пользователей.
     *
     * @param userId идентификатор пользователя (чтобы исключить его запросы).
     * @return список запросов других пользователей в формате DTO.
     */
    List<ItemRequestDto> getAllExceptUser(Long userId, int from, int size);

    /**
     * Обновляет данные запроса.
     *
     * @param id             идентификатор запроса.
     * @param itemRequestDto DTO с обновленными данными.
     * @return обновленный запрос в формате DTO.
     * @throws NotFoundException если запрос не найден.
     */
    ItemRequestDto update(Long id, ItemRequestDto itemRequestDto);

    /**
     * Удаляет запрос.
     *
     * @param id идентификатор запроса для удаления.
     */
    void delete(Long id);


}