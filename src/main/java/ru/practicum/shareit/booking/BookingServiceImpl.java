package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    public BookingDto create(BookingCreateDto bookingCreateDto, Long bookerId) {
        // Проверяем существование пользователя и вещи
        userService.getUserById(bookerId);
        itemService.getItemById(bookingCreateDto.getItemId());

        Booking booking = BookingMapper.toBooking(bookingCreateDto, bookerId);
        Booking savedBooking = bookingRepository.save(booking);
        return BookingMapper.toBookingDto(savedBooking);
    }

    @Override
    public BookingDto getById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Booking not found"));
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto updateStatus(Long id, Boolean approved, Long ownerId) {
        // TODO: реализовать логику подтверждения/отклонения
        return null;
    }

    @Override
    public List<BookingDto> getByBookerId(Long bookerId, String state) {
        userService.getUserById(bookerId);
        return bookingRepository.findByBookerId(bookerId).stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getByOwnerId(Long ownerId, String state) {
        userService.getUserById(ownerId);
        return bookingRepository.findByItemOwnerId(ownerId).stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        bookingRepository.deleteById(id);
    }
}