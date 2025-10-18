package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Интерфейс репозитория для работы с запросами вещей.
 */
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    /**
     * Находит все запросы определенного пользователя, отсортированные по дате создания (новые сначала).
     */
    @Query("SELECT ir FROM ItemRequest ir WHERE ir.requester.id = :requesterId ORDER BY ir.created DESC")
    List<ItemRequest> findByRequesterIdOrderByCreatedDesc(Long requesterId);

    /**
     * Находит все запросы, созданные другими пользователями, отсортированные по дате создания (новые сначала).
     */
    @Query("SELECT ir FROM ItemRequest ir WHERE ir.requester.id != :userId ORDER BY ir.created DESC")
    List<ItemRequest> findByRequesterIdNotOrderByCreatedDesc(Long userId);
}