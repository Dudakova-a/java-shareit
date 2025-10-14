package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import ru.practicum.shareit.client.BookingClient;

@Slf4j
@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody BookingCreateDto bookingCreateDto,
                                         @RequestHeader("X-Sharer-User-Id") Long bookerId) {
        log.info("POST /bookings - Creating booking: {}, bookerId: {}", bookingCreateDto, bookerId);
        ResponseEntity<Object> response = bookingClient.bookItem(bookerId, bookingCreateDto);
        log.info("POST /bookings - Booking creation completed with status: {}", response.getStatusCode());
        return response;
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long bookingId) {
        log.info("GET /bookings/{} - Getting booking by id, userId: {}", bookingId, userId);
        ResponseEntity<Object> response = bookingClient.getBooking(userId, bookingId);
        log.info("GET /bookings/{} - Booking retrieval completed with status: {}", bookingId, response.getStatusCode());
        return response;
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateStatus(@PathVariable Long bookingId,
                                               @RequestParam Boolean approved,
                                               @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("PATCH /bookings/{} - Updating status: approved={}, userId: {}", bookingId, approved, userId);
        ResponseEntity<Object> response = bookingClient.updateStatus(bookingId, approved, userId);
        log.info("PATCH /bookings/{} - Status update completed with status: {}", bookingId, response.getStatusCode());
        return response;
    }

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestParam(defaultValue = "ALL") String state,
                                              @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                              @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("GET /bookings - Getting bookings for userId: {}, state: {}, from: {}, size: {}",
                userId, state, from, size);
        ResponseEntity<Object> response = bookingClient.getBookings(userId, state, from, size);
        log.info("GET /bookings - Found bookings for userId: {}, response status: {}", userId, response.getStatusCode());
        return response;
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @RequestParam(defaultValue = "ALL") String state,
                                                   @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                   @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("GET /bookings/owner - Getting owner bookings for userId: {}, state: {}, from: {}, size: {}",
                userId, state, from, size);
        ResponseEntity<Object> response = bookingClient.getOwnerBookings(userId, state, from, size);
        log.info("GET /bookings/owner - Found owner bookings for userId: {}, response status: {}", userId, response.getStatusCode());
        return response;
    }

    @DeleteMapping("/{bookingId}")
    public ResponseEntity<Object> delete(@PathVariable Long bookingId) {
        log.info("DELETE /bookings/{} - Deleting booking", bookingId);
        ResponseEntity<Object> response = bookingClient.delete(bookingId);
        log.info("DELETE /bookings/{} - Booking deletion completed with status: {}", bookingId, response.getStatusCode());
        return response;
    }
}