package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingCreateDto;

public class BookingMapper {
    // Запрещаем создание экземпляров класса
    private BookingMapper() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItemId(),
                booking.getBookerId(),
                booking.getStatus()
        );
    }

    public static Booking toBooking(BookingCreateDto bookingCreateDto, Long bookerId) {
        Booking booking = new Booking();
        booking.setStart(bookingCreateDto.getStart());
        booking.setEnd(bookingCreateDto.getEnd());
        booking.setItemId(bookingCreateDto.getItemId());
        booking.setBookerId(bookerId);
        booking.setStatus("WAITING"); // по умолчанию
        return booking;
    }
}