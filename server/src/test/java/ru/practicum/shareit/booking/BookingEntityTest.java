package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BookingEntityTest {

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void shouldSaveAndRetrieveBooking() {
        // Given - создаем пользователей и вещь
        User owner = User.builder()
                .name("Owner")
                .email("owner@mail.com")
                .build();
        entityManager.persistAndFlush(owner);

        User booker = User.builder()
                .name("Booker")
                .email("booker@mail.com")
                .build();
        entityManager.persistAndFlush(booker);

        Item item = Item.builder()
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .owner(owner)
                .build();
        entityManager.persistAndFlush(item);

        // When - создаем бронирование
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        Booking booking = Booking.builder()
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();

        Booking savedBooking = entityManager.persistAndFlush(booking);

        // Then - проверяем сохранение
        assertThat(savedBooking.getId()).isNotNull();
        assertThat(savedBooking.getStart()).isEqualTo(start);
        assertThat(savedBooking.getEnd()).isEqualTo(end);
        assertThat(savedBooking.getItem().getId()).isEqualTo(item.getId());
        assertThat(savedBooking.getBooker().getId()).isEqualTo(booker.getId());
        assertThat(savedBooking.getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void shouldUpdateBookingStatus() {
        // Given - сохраняем бронирование
        User owner = createUser("owner@mail.com", "Owner");
        User booker = createUser("booker@mail.com", "Booker");
        Item item = createItem("Item", "Description", true, owner);

        Booking booking = Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();

        Booking savedBooking = entityManager.persistAndFlush(booking);

        // When - обновляем статус
        savedBooking.setStatus(BookingStatus.APPROVED);
        entityManager.persistAndFlush(savedBooking);

        // Then - проверяем обновление
        Booking updatedBooking = entityManager.find(Booking.class, savedBooking.getId());
        assertThat(updatedBooking.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void shouldHaveCorrectColumnMappings() {
        // Given
        User owner = createUser("owner@mail.com", "Owner");
        User booker = createUser("booker@mail.com", "Booker");
        Item item = createItem("Item", "Description", true, owner);

        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime end = LocalDateTime.of(2024, 1, 2, 10, 0);

        Booking booking = Booking.builder()
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(BookingStatus.REJECTED)
                .build();

        // When
        Booking savedBooking = entityManager.persistAndFlush(booking);

        // Then - проверяем маппинг полей
        assertThat(savedBooking.getStart()).isEqualTo(start);
        assertThat(savedBooking.getEnd()).isEqualTo(end);
        assertThat(savedBooking.getStatus()).isEqualTo(BookingStatus.REJECTED);

        // Проверяем связи
        assertThat(savedBooking.getItem()).isNotNull();
        assertThat(savedBooking.getBooker()).isNotNull();
    }

    @Test
    void shouldHandleAllBookingStatuses() {
        // Given
        User owner = createUser("owner@mail.com", "Owner");
        User booker = createUser("booker@mail.com", "Booker");
        Item item = createItem("Item", "Description", true, owner);

        // When & Then - проверяем все возможные статусы
        for (BookingStatus status : BookingStatus.values()) {
            Booking booking = Booking.builder()
                    .start(LocalDateTime.now().plusDays(1))
                    .end(LocalDateTime.now().plusDays(2))
                    .item(item)
                    .booker(booker)
                    .status(status)
                    .build();

            Booking savedBooking = entityManager.persistAndFlush(booking);
            assertThat(savedBooking.getStatus()).isEqualTo(status);

            entityManager.clear(); // Очищаем контекст для следующей итерации
        }
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
}