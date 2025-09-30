package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public BookingWithUserDto create(BookingCreateDto bookingCreateDto, Long bookerId) {  // ← ИЗМЕНИ ТИП
        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + bookerId));

        Item item = itemRepository.findById(bookingCreateDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found with id: " + bookingCreateDto.getItemId()));

        // Проверка доступности вещи
        if (!item.getAvailable()) {
            throw new ValidationException("Item is not available for booking");
        }

        // Проверка что пользователь не бронирует свою вещь
        if (item.getOwner().getId().equals(bookerId)) {
            throw new NotFoundException("Owner cannot book own item");
        }

        // Валидация дат
        if (bookingCreateDto.getStart().isAfter(bookingCreateDto.getEnd()) ||
                bookingCreateDto.getStart().isEqual(bookingCreateDto.getEnd())) {
            throw new ValidationException("Invalid booking dates");
        }

        // Проверка на пересекающиеся бронирования
        if (bookingRepository.existOverlappingBookings(
                bookingCreateDto.getItemId(),
                bookingCreateDto.getStart(),
                bookingCreateDto.getEnd(),
                null)) {
            throw new ValidationException("Item is already booked for this period");
        }

        Booking booking = BookingMapper.toBooking(bookingCreateDto, booker, item);
        Booking savedBooking = bookingRepository.save(booking);
        return BookingMapper.toBookingWithUserDto(savedBooking);  // ← ИСПОЛЬЗУЙ toBookingWithUserDto
    }

    public BookingWithUserDto getById(Long id) {  // ← ИЗМЕНИ ТИП
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Booking not found"));
        return BookingMapper.toBookingWithUserDto(booking);  // ← ИСПОЛЬЗУЙ toBookingWithUserDto
    }

    public BookingWithUserDto updateStatus(Long id, Boolean approved, Long ownerId) {  // ← ИЗМЕНИ ТИП
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        // Проверка что пользователь - владелец вещи
        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new AccessDeniedException("Only item owner can approve/reject booking");
        }

        // Проверка что статус еще WAITING
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Booking status already decided");
        }

        BookingStatus newStatus = approved ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        booking.setStatus(newStatus);

        Booking updatedBooking = bookingRepository.save(booking);
        return BookingMapper.toBookingWithUserDto(updatedBooking);  // ← ИСПОЛЬЗУЙ toBookingWithUserDto
    }

    public List<BookingWithUserDto> getByBookerId(Long bookerId, String state) {  // ← ИЗМЕНИ ТИП
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
                .map(BookingMapper::toBookingWithUserDto)  // ← ИСПОЛЬЗУЙ toBookingWithUserDto
                .collect(Collectors.toList());
    }

    public List<BookingWithUserDto> getByOwnerId(Long ownerId, String state) {  // ← ИЗМЕНИ ТИП
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
                .map(BookingMapper::toBookingWithUserDto)  // ← ИСПОЛЬЗУЙ toBookingWithUserDto
                .collect(Collectors.toList());
    }

    public void delete(Long id) {
        if (!bookingRepository.existsById(id)) {
            throw new NotFoundException("Booking not found");
        }
        bookingRepository.deleteById(id);
    }
}