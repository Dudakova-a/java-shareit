package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
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
        log.info("POST /bookings - Creating booking: {}, bookerId: {}", bookingCreateDto, bookerId);
        BookingDto result = bookingService.create(bookingCreateDto, bookerId);
        log.info("POST /bookings - Booking created successfully: {}", result);
        return result;
    }

    @GetMapping("/{id}")
    public BookingDto getById(@PathVariable Long id) {
        log.info("GET /bookings/{} - Getting booking by id", id);
        BookingDto result = bookingService.getById(id);
        log.info("GET /bookings/{} - Booking found: {}", id, result);
        return result;
    }

    @PatchMapping("/{id}")
    public BookingDto updateStatus(@PathVariable Long id,
                                   @RequestParam Boolean approved,
                                   @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("PATCH /bookings/{} - Updating status: approved={}, ownerId: {}", id, approved, ownerId);
        BookingDto result = bookingService.updateStatus(id, approved, ownerId);
        log.info("PATCH /bookings/{} - Status updated successfully: {}", id, result);
        return result;
    }

    @GetMapping
    public List<BookingDto> getByBookerId(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                          @RequestParam(defaultValue = "ALL") String state) {
        log.info("GET /bookings - Getting bookings for bookerId: {}, state: {}", bookerId, state);
        List<BookingDto> result = bookingService.getByBookerId(bookerId, state);
        log.info("GET /bookings - Found {} bookings for bookerId: {}", result.size(), bookerId);
        return result;
    }

    @GetMapping("/owner")
    public List<BookingDto> getByOwnerId(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                         @RequestParam(defaultValue = "ALL") String state) {
        log.info("GET /bookings/owner - Getting bookings for ownerId: {}, state: {}", ownerId, state);
        List<BookingDto> result = bookingService.getByOwnerId(ownerId, state);
        log.info("GET /bookings/owner - Found {} bookings for ownerId: {}", result.size(), ownerId);
        return result;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.info("DELETE /bookings/{} - Deleting booking", id);
        bookingService.delete(id);
        log.info("DELETE /bookings/{} - Booking deleted successfully", id);
    }
}