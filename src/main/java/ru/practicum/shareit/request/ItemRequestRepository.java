package ru.practicum.shareit.request;


import java.util.List;
import java.util.Optional;

/**
 * Интерфейс репозитория для работы с запросами вещей.
 */
public interface ItemRequestRepository {
    /**
     * Сохраняет новый запрос.
     */
    ItemRequest save(ItemRequest itemRequest);

    /**
     * Находит запрос по идентификатору.
     */
    Optional<ItemRequest> findById(Long id);

    /**
     * Возвращает все запросы.
     */
    List<ItemRequest> findAll();

    /**
     * Находит все запросы определенного пользователя.
     */
    List<ItemRequest> findByRequestorId(Long requestorId);

    /**
     * Находит все запросы, созданные другими пользователями.
     * @param userId идентификатор пользователя, который не должен быть автором.
     * @return список запросов других пользователей.
     */
    List<ItemRequest> findAllExceptRequestor(Long userId);

    /**
     * Обновляет данные запроса.
     */
    ItemRequest update(ItemRequest itemRequest);

    /**
     * Уделяет запрос по идентификатору.
     */
    void deleteById(Long id);
}