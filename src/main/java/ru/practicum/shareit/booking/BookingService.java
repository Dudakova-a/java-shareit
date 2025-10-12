package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingWithUserDto;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public BookingDto create(BookingCreateDto bookingCreateDto, Long bookerId) {
        // Проверка существования пользователя
        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + bookerId));

        // Проверка существования предмета
        Item item = itemRepository.findById(bookingCreateDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found with id: " + bookingCreateDto.getItemId()));

        // Проверка доступности вещи
        if (!item.getAvailable()) {
            throw new ValidationException("Item is not available for booking");
        }

        // Проверка что пользователь не бронирует свою вещь
        if (item.getOwner().getId().equals(bookerId)) {
            throw new AccessDeniedException("Owner cannot book own item");
        }

        // Валидация дат
        validateBookingDates(bookingCreateDto.getStart(), bookingCreateDto.getEnd());

        // Проверка на пересекающиеся бронирования
        if (hasOverlappingBookings(bookingCreateDto.getItemId(), bookingCreateDto.getStart(), bookingCreateDto.getEnd())) {
            throw new ValidationException("Item is already booked for this period");
        }

        // Создание бронирования
        Booking booking = BookingMapper.toBooking(bookingCreateDto, booker, item);
        booking.setStatus(BookingStatus.WAITING);

        Booking savedBooking = bookingRepository.save(booking);
        return BookingMapper.toBookingDto(savedBooking);
    }

    public BookingDto getById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        // Проверка прав доступа
        if (!booking.getBooker().getId().equals(userId) &&
                !booking.getItem().getOwner().getId().equals(userId)) {
            throw new AccessDeniedException("Access denied to booking");
        }

        return BookingMapper.toBookingDto(booking);
    }

    @Transactional
    public BookingDto updateStatus(Long bookingId, Boolean approved, Long ownerId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        // Проверка, что пользователь - владелец вещи
        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new AccessDeniedException("Only item owner can approve/reject booking");
        }

        // Проверка, что статус еще WAITING
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Booking status already decided");
        }

        // Установка нового статуса
        BookingStatus newStatus = approved ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        booking.setStatus(newStatus);

        Booking updatedBooking = bookingRepository.save(booking);
        return BookingMapper.toBookingDto(updatedBooking);
    }

    public List<BookingWithUserDto> getByBookerId(Long bookerId, String state) {
        // Проверка существования пользователя
        userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + bookerId));

        List<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();

        switch (state.toUpperCase()) {
            case "ALL":
                bookings = bookingRepository.findByBookerIdOrderByStartDesc(bookerId);
                break;
            case "CURRENT":
                bookings = bookingRepository.findCurrentBookingsByBookerId(bookerId, now);
                break;
            case "PAST":
                bookings = bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(bookerId, now);
                break;
            case "FUTURE":
                bookings = bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(bookerId, now);
                break;
            case "WAITING":
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(bookerId, BookingStatus.WAITING);
                break;
            case "REJECTED":
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(bookerId, BookingStatus.REJECTED);
                break;
            default:
                throw new ValidationException("Unknown state: " + state);
        }

        return bookings.stream()
                .map(BookingMapper::toBookingWithUserDto)
                .collect(Collectors.toList());
    }

    public List<BookingWithUserDto> getByOwnerId(Long ownerId, String state) {
        // Проверка существования пользователя
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + ownerId));

        List<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();

        switch (state.toUpperCase()) {
            case "ALL":
                bookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(ownerId);
                break;
            case "CURRENT":
                bookings = bookingRepository.findCurrentBookingsByOwnerId(ownerId, now);
                break;
            case "PAST":
                bookings = bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(ownerId, now);
                break;
            case "FUTURE":
                bookings = bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(ownerId, now);
                break;
            case "WAITING":
                bookings = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.WAITING);
                break;
            case "REJECTED":
                bookings = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.REJECTED);
                break;
            default:
                throw new ValidationException("Unknown state: " + state);
        }

        return bookings.stream()
                .map(BookingMapper::toBookingWithUserDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void delete(Long id) {
        if (!bookingRepository.existsById(id)) {
            throw new NotFoundException("Booking not found");
        }
        bookingRepository.deleteById(id);
    }

    private void validateBookingDates(LocalDateTime start, LocalDateTime end) {
        if (start == null) {
            throw new ValidationException("Start date cannot be null");
        }
        if (end == null) {
            throw new ValidationException("End date cannot be null");
        }
        if (start.isBefore(LocalDateTime.now())) {
            throw new ValidationException("Start date cannot be in the past");
        }
        if (end.isBefore(start) || end.isEqual(start)) {
            throw new ValidationException("Invalid booking dates: end must be after start");
        }
    }

    private boolean hasOverlappingBookings(Long itemId, LocalDateTime start, LocalDateTime end) {
        return bookingRepository.existOverlappingBookings(itemId, start, end, null);
    }
}