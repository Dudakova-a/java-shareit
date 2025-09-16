package ru.practicum.shareit.request;

import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

/**
 * In-memory реализация репозитория запросов вещей.
 */
@Repository
public class InMemoryItemRequestRepository implements ItemRequestRepository {
    private final Map<Long, ItemRequest> requests = new HashMap<>();
    private long nextId = 1L;

    /**
     * Сохраняет новый запрос.
     */
    @Override
    public ItemRequest save(ItemRequest itemRequest) {
        itemRequest.setId(nextId++);
        requests.put(itemRequest.getId(), itemRequest);
        return itemRequest;
    }

    /**
     * Находит запрос по идентификатору.
     */
    @Override
    public Optional<ItemRequest> findById(Long id) {
        return Optional.ofNullable(requests.get(id));
    }

    /**
     * Возвращает все запросы.
     */
    @Override
    public List<ItemRequest> findAll() {
        return new ArrayList<>(requests.values());
    }

    /**
     * Находит все запросы определенного пользователя.
     */
    @Override
    public List<ItemRequest> findByRequestorId(Long requestorId) {
        return requests.values().stream()
                .filter(request -> request.getRequestorId().equals(requestorId))
                .sorted(Comparator.comparing(ItemRequest::getCreated).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Находит все запросы, созданные другими пользователями.
     */
    @Override
    public List<ItemRequest> findAllExceptRequestor(Long userId) {
        return requests.values().stream()
                .filter(request -> !request.getRequestorId().equals(userId))
                .sorted(Comparator.comparing(ItemRequest::getCreated).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Обновляет данные запроса.
     */
    @Override
    public ItemRequest update(ItemRequest itemRequest) {
        requests.put(itemRequest.getId(), itemRequest);
        return itemRequest;
    }

    /**
     * Уделяет запрос по идентификатору.
     */
    @Override
    public void deleteById(Long id) {
        requests.remove(id);
    }
}