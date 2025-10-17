package ru.practicum.shareit.item;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.BookingInfoDto;

import java.util.List;

/**
 * Интерфейс сервиса для работы с вещами.
 * Определяет бизнес-логику, специфичную для работы с вещами.
 */
public interface ItemService {
    /**
     * Создает новую вещь от имени указанного владельца.
     *
     * @param itemDto DTO с данными вещи.
     * @param ownerId идентификатор владельца (из заголовка X-Sharer-User-Id).
     * @return созданная вещь в формате DTO.
     * @throws NotFoundException если владелец не найден.
     */
    ItemDto create(ItemDto itemDto, Long ownerId);

    /**
     * Находит вещь по идентификатору.
     *
     * @param id идентификатор вещи.
     * @return вещь в формате DTO.
     * @throws NotFoundException если вещь не найдена.
     */
    ItemDto getById(Long id, Long userId);

    /**
     * Находит все вещи определенного владельца.
     *
     * @param ownerId идентификатор владельца.
     * @return список вещей владельца в формате DTO.
     */
    List<ItemDto> getByOwnerId(Long ownerId);

    /**
     * Обновляет данные вещи (может выполнять только владелец вещи).
     *
     * @param id      идентификатор вещи.
     * @param itemDto DTO с обновленными данными.
     * @param ownerId идентификатор владельца вещи.
     * @return обновленная вещь в формате DTO.
     * @throws NotFoundException если вещь не найдена или пользователь не владелец.
     */
    ItemDto update(Long id, ItemDto itemDto, Long ownerId);

    /**
     * Удаляет вещь.
     *
     * @param id идентификатор вещи для удаления.
     */
    void delete(Long id);

    /**
     * Ищет доступные вещи по тексту.
     *
     * @param text текст для поиска.
     * @return список подходящих вещей в формате DTO.
     */
    List<ItemDto> search(String text);

    /**
     * Добавляет комментарий к вещи.
     *
     * @param itemId     идентификатор вещи
     * @param commentDto DTO комментария
     * @param userId     идентификатор автора комментария
     * @return созданный комментарий
     */
    CommentDto addComment(Long itemId, CommentDto commentDto, Long userId);

    /**
     * Находит последнее завершенное бронирование для вещи.
     */
    BookingInfoDto findLastBooking(Long itemId);

    /**
     * Находит ближайшее следующее бронирование для вещи.
     */
    BookingInfoDto findNextBooking(Long itemId);
}