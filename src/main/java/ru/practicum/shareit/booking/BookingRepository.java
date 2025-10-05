package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.dto.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Интерфейс репозитория для работы с бронированиями.
 */
public interface BookingRepository extends JpaRepository<Booking, Long> {

    /**
     * Находит все бронирования определенного пользователя, отсортированные по дате начала.
     */
    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

    /**
     * Находит бронирования пользователя с определенным статусом, отсортированные по дате начала.
     */
    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

    /**
     * Находит текущие бронирования пользователя.
     */
    @Query("SELECT b FROM Booking b JOIN FETCH b.item JOIN FETCH b.booker WHERE b.booker.id = :bookerId AND b.start <= :start AND b.end >= :start ORDER BY b.start DESC")
    List<Booking> findByBookerIdAndStartBeforeOrderByStartDesc(
            @Param("bookerId") Long bookerId, @Param("start") LocalDateTime start);

    /**
     * Находит завершенные бронирования пользователя.
     */
    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime end);

    /**
     * Находит будущие бронирования пользователя.
     */
    @Query("SELECT b FROM Booking b JOIN FETCH b.item JOIN FETCH b.booker WHERE b.booker.id = :bookerId AND b.start >= :start ORDER BY b.start DESC")
    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(@Param("bookerId") Long bookerId, @Param("start") LocalDateTime start);

    /**
     * Находит все бронирования вещей владельца, отсортированные по дате начала.
     */
    @Query("SELECT b FROM Booking b JOIN FETCH b.item JOIN FETCH b.booker WHERE b.item.owner.id = :ownerId ORDER BY b.start DESC")
    List<Booking> findByItemOwnerIdOrderByStartDesc(@Param("ownerId") Long ownerId);

    /**
     * Находит бронирования вещей владельца с определенным статусом.
     */
    @Query("SELECT b FROM Booking b JOIN FETCH b.item JOIN FETCH b.booker WHERE b.item.owner.id = :ownerId AND b.status = :status ORDER BY b.start DESC")
    List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(@Param("ownerId") Long ownerId, @Param("status") BookingStatus status);

    /**
     * Находит текущие бронирования вещей владельца.
     */
    @Query("SELECT b FROM Booking b JOIN FETCH b.item JOIN FETCH b.booker WHERE b.item.owner.id = :ownerId AND b.start <= :start AND b.end >= :start ORDER BY b.start DESC")
    List<Booking> findByItemOwnerIdAndStartBeforeOrderByStartDesc(
            @Param("ownerId") Long ownerId, @Param("start") LocalDateTime start);

    /**
     * Находит завершенные бронирования вещей владельца.
     */
    @Query("SELECT b FROM Booking b JOIN FETCH b.item JOIN FETCH b.booker WHERE b.item.owner.id = :ownerId AND b.end <= :end ORDER BY b.start DESC")
    List<Booking> findByItemOwnerIdAndEndBeforeOrderByStartDesc(@Param("ownerId") Long ownerId, @Param("end") LocalDateTime end);

    /**
     * Находит будущие бронирования вещей владельца.
     */
    @Query("SELECT b FROM Booking b JOIN FETCH b.item JOIN FETCH b.booker WHERE b.item.owner.id = :ownerId AND b.start >= :start ORDER BY b.start DESC")
    List<Booking> findByItemOwnerIdAndStartAfterOrderByStartDesc(@Param("ownerId") Long ownerId, @Param("start") LocalDateTime start);

    // === ДОБАВЛЯЕМ МЕТОДЫ ДЛЯ CURRENT СТАТУСА ===

    /**
     * Находит текущие бронирования пользователя (начались, но еще не закончились).
     */
    @Query("SELECT b FROM Booking b JOIN FETCH b.item JOIN FETCH b.booker " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.start <= :now " +
            "AND b.end >= :now " +
            "ORDER BY b.start DESC")
    List<Booking> findCurrentBookingsByBookerId(@Param("bookerId") Long bookerId,
                                                @Param("now") LocalDateTime now);

    /**
     * Находит текущие бронирования вещей владельца (начались, но еще не закончились).
     */
    @Query("SELECT b FROM Booking b JOIN FETCH b.item JOIN FETCH b.booker " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND b.start <= :now " +
            "AND b.end >= :now " +
            "ORDER BY b.start DESC")
    List<Booking> findCurrentBookingsByOwnerId(@Param("ownerId") Long ownerId,
                                               @Param("now") LocalDateTime now);

    /**
     * Находит пересекающиеся бронирования для указанной вещи.
     * Используется для проверки доступности вещи в заданный период.
     */
    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN TRUE ELSE FALSE END FROM Booking b WHERE " +
            "b.item.id = :itemId AND b.status = 'APPROVED' " +
            "AND (:excludeBookingId IS NULL OR b.id <> :excludeBookingId) " +
            "AND ((b.start BETWEEN :start AND :end) OR (b.end BETWEEN :start AND :end) " +
            "OR (b.start <= :start AND b.end >= :end))")
    boolean existOverlappingBookings(@Param("itemId") Long itemId,
                                     @Param("start") LocalDateTime start,
                                     @Param("end") LocalDateTime end,
                                     @Param("excludeBookingId") Long excludeBookingId);

    /**
     * Находит последнее завершенное бронирование вещи пользователем.
     * Используется для проверки возможности оставить отзыв.
     */
    @Query("SELECT b FROM Booking b JOIN FETCH b.item JOIN FETCH b.booker WHERE b.item.id = :itemId AND b.booker.id = :bookerId AND b.status = :status AND b.end <= :end ORDER BY b.end DESC")
    Optional<Booking> findFirstByItemIdAndBookerIdAndStatusAndEndBefore(
            @Param("itemId") Long itemId, @Param("bookerId") Long bookerId, @Param("status") BookingStatus status, @Param("end") LocalDateTime end);

    /**
     * Находит завершенные бронирования вещи, отсортированные по дате окончания (от новых к старым).
     */
    @Query("SELECT b FROM Booking b JOIN FETCH b.item JOIN FETCH b.booker WHERE b.item.id = :itemId AND b.end < :currentTime AND b.status = 'APPROVED' ORDER BY b.end DESC")
    List<Booking> findCompletedBookingsByItemId(@Param("itemId") Long itemId, @Param("currentTime") LocalDateTime currentTime);

    /**
     * Находит будущие бронирования вещи, отсортированные по дате начала (от ближайших к дальним).
     */
    @Query("SELECT b FROM Booking b JOIN FETCH b.item JOIN FETCH b.booker WHERE b.item.id = :itemId AND b.start > :currentTime AND b.status = 'APPROVED' ORDER BY b.start ASC")
    List<Booking> findFutureBookingsByItemId(@Param("itemId") Long itemId, @Param("currentTime") LocalDateTime currentTime);

    /**
     * Находит текущие бронирования вещи.
     */
    @Query("SELECT b FROM Booking b WHERE b.item.id IN :itemIds AND b.end < :currentTime " +
            "AND b.status = 'APPROVED' ORDER BY b.end DESC")
    List<Booking> findCurrentBookingsByItemId(@Param("itemIds") List<Long> itemIds, @Param("currentTime") LocalDateTime currentTime);

    List<Booking> findByItemIdInAndStatusOrderByStartAsc(List<Long> itemIds, BookingStatus status);
}