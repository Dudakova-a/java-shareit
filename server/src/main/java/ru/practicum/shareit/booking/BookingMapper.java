package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingWithUserDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

public class BookingMapper {
    private BookingMapper() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .itemId(booking.getItem().getId())
                .bookerId(booking.getBooker().getId())
                .status(booking.getStatus().name())
                .booker(mapToBooker(booking.getBooker()))
                .item(mapToItem(booking.getItem()))
                .build();
    }

    public static Booking toBooking(BookingCreateDto bookingCreateDto, User booker, Item item) {
        Booking booking = new Booking();
        booking.setStart(bookingCreateDto.getStart());
        booking.setEnd(bookingCreateDto.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        return booking;
    }

    public static BookingWithUserDto toBookingWithUserDto(Booking booking) {
        return BookingWithUserDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(ItemDto.builder()
                        .id(booking.getItem().getId())
                        .name(booking.getItem().getName())
                        .description(booking.getItem().getDescription())
                        .available(booking.getItem().getAvailable())
                        .build())
                .booker(new UserDto(
                        booking.getBooker().getId(),
                        booking.getBooker().getName(),
                        booking.getBooker().getEmail()
                ))
                .status(booking.getStatus().name())
                .build();
    }

    // ДОБАВИТЬ эти вспомогательные методы
    private static BookingDto.Booker mapToBooker(User user) {
        if (user == null) {
            return null;
        }

        return BookingDto.Booker.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    private static BookingDto.Item mapToItem(Item item) {
        if (item == null) {
            return null;
        }

        return BookingDto.Item.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .build();
    }
}