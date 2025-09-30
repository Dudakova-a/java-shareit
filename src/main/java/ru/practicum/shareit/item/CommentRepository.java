package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Интерфейс репозитория для работы с комментариями.
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {
    /**
     * Находит все комментарии для указанной вещи, отсортированные по дате создания.
     */
    @Query("SELECT c FROM Comment c JOIN FETCH c.author WHERE c.item.id = :itemId ORDER BY c.created DESC")
    List<Comment> findByItemIdOrderByCreatedDesc(@Param("itemId") Long itemId);

    /**
     * Находит все комментарии для списка вещей.
     */
    @Query("SELECT c FROM Comment c JOIN FETCH c.author WHERE c.item.id IN :itemIds ORDER BY c.item.id, c.created DESC")
    List<Comment> findByItemIdInOrderByCreatedDesc(@Param("itemIds") List<Long> itemIds);

    /**
     * Проверяет, существует ли комментарий от пользователя к вещи.
     */
    @Query("SELECT COUNT(c) > 0 FROM Comment c WHERE c.author.id = :userId AND c.item.id = :itemId")
    boolean existsByAuthorIdAndItemId(@Param("userId") Long userId, @Param("itemId") Long itemId);

}