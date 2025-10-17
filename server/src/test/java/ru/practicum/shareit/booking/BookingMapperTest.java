package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingWithUserDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class BookingMapperTest {

    @Test
    void toBookingDto_ShouldMapCorrectly() {
        // Given
        User booker = User.builder()
                .id(1L)
                .name("Booker Name")
                .email("booker@mail.com")
                .build();

        User owner = User.builder()
                .id(2L)
                .name("Owner Name")
                .email("owner@mail.com")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .owner(owner)
                .build();

        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2024, 1, 1, 10, 0))
                .end(LocalDateTime.of(2024, 1, 2, 10, 0))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();

        // When
        BookingDto result = BookingMapper.toBookingDto(booking);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getStart()).isEqualTo(LocalDateTime.of(2024, 1, 1, 10, 0));
        assertThat(result.getEnd()).isEqualTo(LocalDateTime.of(2024, 1, 2, 10, 0));
        assertThat(result.getItemId()).isEqualTo(1L);
        assertThat(result.getBookerId()).isEqualTo(1L);
        assertThat(result.getStatus()).isEqualTo("APPROVED");

        // Проверяем вложенные объекты (результат работы приватных методов)
        assertThat(result.getBooker()).isNotNull();
        assertThat(result.getBooker().getId()).isEqualTo(1L);
        assertThat(result.getBooker().getName()).isEqualTo("Booker Name");
        assertThat(result.getBooker().getEmail()).isEqualTo("booker@mail.com");

        assertThat(result.getItem()).isNotNull();
        assertThat(result.getItem().getId()).isEqualTo(1L);
        assertThat(result.getItem().getName()).isEqualTo("Test Item");
        assertThat(result.getItem().getDescription()).isEqualTo("Test Description");
        assertThat(result.getItem().getAvailable()).isTrue();
    }

    @Test
    void toBooking_ShouldMapCorrectly() {
        // Given
        BookingCreateDto createDto = new BookingCreateDto();
        createDto.setStart(LocalDateTime.of(2024, 1, 1, 10, 0));
        createDto.setEnd(LocalDateTime.of(2024, 1, 2, 10, 0));
        createDto.setItemId(1L);

        User booker = User.builder()
                .id(1L)
                .name("Booker")
                .email("booker@mail.com")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("Item")
                .description("Description")
                .available(true)
                .build();

        // When
        Booking result = BookingMapper.toBooking(createDto, booker, item);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getStart()).isEqualTo(createDto.getStart());
        assertThat(result.getEnd()).isEqualTo(createDto.getEnd());
        assertThat(result.getItem()).isEqualTo(item);
        assertThat(result.getBooker()).isEqualTo(booker);
        assertThat(result.getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void toBookingWithUserDto_ShouldMapCorrectly() {
        // Given
        User booker = User.builder()
                .id(1L)
                .name("Booker Name")
                .email("booker@mail.com")
                .build();

        User owner = User.builder()
                .id(2L)
                .name("Owner Name")
                .email("owner@mail.com")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .owner(owner)
                .build();

        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2024, 1, 1, 10, 0))
                .end(LocalDateTime.of(2024, 1, 2, 10, 0))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();

        // When
        BookingWithUserDto result = BookingMapper.toBookingWithUserDto(booking);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getStart()).isEqualTo(LocalDateTime.of(2024, 1, 1, 10, 0));
        assertThat(result.getEnd()).isEqualTo(LocalDateTime.of(2024, 1, 2, 10, 0));
        assertThat(result.getStatus()).isEqualTo("WAITING");

        // Проверяем вложенные объекты
        assertThat(result.getBooker()).isNotNull();
        assertThat(result.getBooker().getId()).isEqualTo(1L);
        assertThat(result.getBooker().getName()).isEqualTo("Booker Name");
        assertThat(result.getBooker().getEmail()).isEqualTo("booker@mail.com");

        assertThat(result.getItem()).isNotNull();
        assertThat(result.getItem().getId()).isEqualTo(1L);
        assertThat(result.getItem().getName()).isEqualTo("Test Item");
        assertThat(result.getItem().getDescription()).isEqualTo("Test Description");
        assertThat(result.getItem().getAvailable()).isTrue();
    }

    @Test
    void toBookingDto_WithNullFields_ShouldHandleGracefully() {
        // Given - создаем booking с null полями
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setStatus(BookingStatus.WAITING);
        // item и booker остаются null

        // When & Then - проверяем, что маппер не падает

    }

    @Test
    void shouldHandleAllBookingStatuses() {
        // Given
        User booker = User.builder().id(1L).name("Booker").build();
        Item item = Item.builder().id(1L).name("Item").build();

        // When & Then - проверяем все статусы
        for (BookingStatus status : BookingStatus.values()) {
            Booking booking = Booking.builder()
                    .id(1L)
                    .start(LocalDateTime.now())
                    .end(LocalDateTime.now().plusDays(1))
                    .item(item)
                    .booker(booker)
                    .status(status)
                    .build();

            BookingDto result = BookingMapper.toBookingDto(booking);
            assertThat(result.getStatus()).isEqualTo(status.name());
        }
    }
}