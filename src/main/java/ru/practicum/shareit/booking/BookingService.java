package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingCreateDto;

import java.util.List;

public interface BookingService {
    BookingDto create(BookingCreateDto bookingCreateDto, Long bookerId);

    BookingDto getById(Long id);

    BookingDto updateStatus(Long id, Boolean approved, Long ownerId);

    List<BookingDto> getByBookerId(Long bookerId, String state);

    List<BookingDto> getByOwnerId(Long ownerId, String state);

    void delete(Long id);
}