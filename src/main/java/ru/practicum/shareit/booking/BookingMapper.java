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
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem().getId(),
                booking.getBooker().getId(),
                booking.getStatus().name()
        );
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
}