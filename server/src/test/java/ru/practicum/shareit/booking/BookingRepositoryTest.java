package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookingRepository bookingRepository;

    @Test
    void findByBookerIdOrderByStartDesc_ShouldReturnBookings() {
        // Given
        User booker = createUser("booker@mail.com", "Booker");
        User owner = createUser("owner@mail.com", "Owner");
        Item item = createItem("Item", "Description", true, owner);

        Booking booking1 = createBooking(
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                item, booker, BookingStatus.WAITING
        );

        Booking booking2 = createBooking(
                LocalDateTime.now().plusDays(3),
                LocalDateTime.now().plusDays(4),
                item, booker, BookingStatus.APPROVED
        );

        // When
        List<Booking> result = bookingRepository.findByBookerIdOrderByStartDesc(booker.getId());

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getStart()).isAfter(result.get(1).getStart()); // Проверяем сортировку
    }

    @Test
    void findByBookerIdAndStatusOrderByStartDesc_ShouldReturnFilteredBookings() {
        // Given
        User booker = createUser("booker@mail.com", "Booker");
        User owner = createUser("owner@mail.com", "Owner");
        Item item = createItem("Item", "Description", true, owner);

        Booking waitingBooking = createBooking(
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                item, booker, BookingStatus.WAITING
        );

        Booking approvedBooking = createBooking(
                LocalDateTime.now().plusDays(3),
                LocalDateTime.now().plusDays(4),
                item, booker, BookingStatus.APPROVED
        );

        // When
        List<Booking> waitingResult = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                booker.getId(), BookingStatus.WAITING);
        List<Booking> approvedResult = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                booker.getId(), BookingStatus.APPROVED);

        // Then
        assertThat(waitingResult).hasSize(1);
        assertThat(waitingResult.get(0).getStatus()).isEqualTo(BookingStatus.WAITING);

        assertThat(approvedResult).hasSize(1);
        assertThat(approvedResult.get(0).getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void findCurrentBookingsByBookerId_ShouldReturnCurrentBookings() {
        // Given
        User booker = createUser("booker@mail.com", "Booker");
        User owner = createUser("owner@mail.com", "Owner");
        Item item = createItem("Item", "Description", true, owner);

        LocalDateTime now = LocalDateTime.now();

        // Текущее бронирование (началось, но не закончилось)
        Booking currentBooking = createBooking(
                now.minusDays(1),
                now.plusDays(1),
                item, booker, BookingStatus.APPROVED
        );

        // Прошлое бронирование
        createBooking(
                now.minusDays(3),
                now.minusDays(2),
                item, booker, BookingStatus.APPROVED
        );

        // When
        List<Booking> result = bookingRepository.findCurrentBookingsByBookerId(booker.getId(), now);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(currentBooking.getId());
    }

    @Test
    void findByBookerIdAndEndBeforeOrderByStartDesc_ShouldReturnPastBookings() {
        // Given
        User booker = createUser("booker@mail.com", "Booker");
        User owner = createUser("owner@mail.com", "Owner");
        Item item = createItem("Item", "Description", true, owner);

        LocalDateTime now = LocalDateTime.now();

        Booking pastBooking = createBooking(
                now.minusDays(3),
                now.minusDays(2),
                item, booker, BookingStatus.APPROVED
        );

        // Будущее бронирование
        createBooking(
                now.plusDays(1),
                now.plusDays(2),
                item, booker, BookingStatus.WAITING
        );

        // When
        List<Booking> result = bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(
                booker.getId(), now);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(pastBooking.getId());
    }

    @Test
    void findByItemOwnerIdOrderByStartDesc_ShouldReturnOwnerBookings() {
        // Given
        User owner = createUser("owner@mail.com", "Owner");
        User booker1 = createUser("booker1@mail.com", "Booker1");
        User booker2 = createUser("booker2@mail.com", "Booker2");

        Item item1 = createItem("Item1", "Description1", true, owner);
        Item item2 = createItem("Item2", "Description2", true, owner);

        createBooking(
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                item1, booker1, BookingStatus.WAITING
        );

        createBooking(
                LocalDateTime.now().plusDays(3),
                LocalDateTime.now().plusDays(4),
                item2, booker2, BookingStatus.APPROVED
        );

        // When
        List<Booking> result = bookingRepository.findByItemOwnerIdOrderByStartDesc(owner.getId());

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(booking ->
                booking.getItem().getOwner().getId().equals(owner.getId()));
    }

    @Test
    void existOverlappingBookings_ShouldDetectOverlaps() {
        // Given
        User owner = createUser("owner@mail.com", "Owner");
        User booker = createUser("booker@mail.com", "Booker");
        Item item = createItem("Item", "Description", true, owner);

        LocalDateTime baseStart = LocalDateTime.now().plusDays(1);
        LocalDateTime baseEnd = LocalDateTime.now().plusDays(3);

        // Существующее бронирование
        createBooking(baseStart, baseEnd, item, booker, BookingStatus.APPROVED);

        // When & Then - проверяем различные случаи пересечения
        // Полное пересечение
        boolean result1 = bookingRepository.existOverlappingBookings(
                item.getId(), baseStart.plusHours(1), baseEnd.minusHours(1), null);
        assertThat(result1).isTrue();

        // Начало внутри существующего
        boolean result2 = bookingRepository.existOverlappingBookings(
                item.getId(), baseStart.plusHours(1), baseEnd.plusDays(1), null);
        assertThat(result2).isTrue();

        // Конец внутри существующего
        boolean result3 = bookingRepository.existOverlappingBookings(
                item.getId(), baseStart.minusDays(1), baseEnd.minusHours(1), null);
        assertThat(result3).isTrue();

        // Нет пересечения
        boolean result4 = bookingRepository.existOverlappingBookings(
                item.getId(), baseEnd.plusDays(1), baseEnd.plusDays(2), null);
        assertThat(result4).isFalse();
    }

    @Test
    void findFirstByItemIdAndBookerIdAndStatusAndEndBefore_ShouldReturnLastCompletedBooking() {
        // Given
        User owner = createUser("owner@mail.com", "Owner");
        User booker = createUser("booker@mail.com", "Booker");
        Item item = createItem("Item", "Description", true, owner);

        LocalDateTime now = LocalDateTime.now();

        Booking completedBooking = createBooking(
                now.minusDays(3),
                now.minusDays(1),
                item, booker, BookingStatus.APPROVED
        );

        // When
        Optional<Booking> result = bookingRepository.findFirstByItemIdAndBookerIdAndStatusAndEndBefore(
                item.getId(), booker.getId(), BookingStatus.APPROVED, now);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(completedBooking.getId());
    }

    @Test
    void findCompletedBookingsByItemId_ShouldReturnCompletedBookings() {
        // Given
        User owner = createUser("owner@mail.com", "Owner");
        User booker = createUser("booker@mail.com", "Booker");
        Item item = createItem("Item", "Description", true, owner);

        LocalDateTime now = LocalDateTime.now();

        Booking completed1 = createBooking(
                now.minusDays(5),
                now.minusDays(4),
                item, booker, BookingStatus.APPROVED
        );

        Booking completed2 = createBooking(
                now.minusDays(3),
                now.minusDays(2),
                item, booker, BookingStatus.APPROVED
        );

        // Будущее бронирование
        createBooking(
                now.plusDays(1),
                now.plusDays(2),
                item, booker, BookingStatus.APPROVED
        );

        // When
        List<Booking> result = bookingRepository.findCompletedBookingsByItemId(item.getId(), now);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getEnd()).isAfter(result.get(1).getEnd()); // Сортировка по убыванию даты окончания
    }

    // Вспомогательные методы
    private User createUser(String email, String name) {
        User user = User.builder()
                .email(email)
                .name(name)
                .build();
        return entityManager.persistAndFlush(user);
    }

    private Item createItem(String name, String description, Boolean available, User owner) {
        Item item = Item.builder()
                .name(name)
                .description(description)
                .available(available)
                .owner(owner)
                .build();
        return entityManager.persistAndFlush(item);
    }

    private Booking createBooking(LocalDateTime start, LocalDateTime end, Item item, User booker, BookingStatus status) {
        Booking booking = Booking.builder()
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(status)
                .build();
        return entityManager.persistAndFlush(booking);
    }
}