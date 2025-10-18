package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingWithUserDto;
import ru.practicum.shareit.booking.dto.BookingCreateDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(@RequestBody BookingCreateDto bookingCreateDto,
                             @RequestHeader("X-Sharer-User-Id") Long bookerId) {
        log.info("POST /bookings - Creating booking for item: {}, bookerId: {}",
                bookingCreateDto.getItemId(), bookerId);
        BookingDto result = bookingService.create(bookingCreateDto, bookerId);
        log.info("POST /bookings - Booking created successfully with id: {}", result.getId());
        return result;
    }

    @GetMapping("/{id}")
    public BookingDto getById(@PathVariable Long id,
                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET /bookings/{} - Getting booking by id, userId: {}", id, userId);
        BookingDto result = bookingService.getById(id, userId);
        log.info("GET /bookings/{} - Booking found for user: {}", id, userId);
        return result;
    }

    @PatchMapping("/{id}")
    public BookingDto updateStatus(@PathVariable Long id,
                                   @RequestParam Boolean approved,
                                   @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("PATCH /bookings/{} - Updating status to: {}, userId: {}", id, approved, userId);
        BookingDto result = bookingService.updateStatus(id, approved, userId);
        log.info("PATCH /bookings/{} - Status updated successfully by user: {}", id, userId);
        return result;
    }

    @GetMapping
    public List<BookingWithUserDto> getByBookerId(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                                  @RequestParam(defaultValue = "ALL") String state) {
        log.info("GET /bookings - Getting bookings for bookerId: {}, state: {}", bookerId, state);
        List<BookingWithUserDto> result = bookingService.getByBookerId(bookerId, state);
        log.info("GET /bookings - Found {} bookings for bookerId: {} with state: {}",
                result.size(), bookerId, state);
        return result;
    }

    @GetMapping("/owner")
    public List<BookingWithUserDto> getByOwnerId(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                 @RequestParam(defaultValue = "ALL") String state) {
        log.info("GET /bookings/owner - Getting bookings for ownerId: {}, state: {}", ownerId, state);
        List<BookingWithUserDto> result = bookingService.getByOwnerId(ownerId, state);
        log.info("GET /bookings/owner - Found {} bookings for ownerId: {} with state: {}",
                result.size(), ownerId, state);
        return result;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.info("DELETE /bookings/{} - Deleting booking", id);
        bookingService.delete(id);
        log.info("DELETE /bookings/{} - Booking deleted successfully", id);
    }
}