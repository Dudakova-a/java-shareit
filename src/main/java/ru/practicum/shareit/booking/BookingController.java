package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingCreateDto;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(@RequestBody BookingCreateDto bookingCreateDto,
                             @RequestHeader("X-Sharer-User-Id") Long bookerId) {
        return bookingService.create(bookingCreateDto, bookerId);
    }

    @GetMapping("/{id}")
    public BookingDto getById(@PathVariable Long id) {
        return bookingService.getById(id);
    }

    @PatchMapping("/{id}")
    public BookingDto updateStatus(@PathVariable Long id,
                                   @RequestParam Boolean approved,
                                   @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return bookingService.updateStatus(id, approved, ownerId);
    }

    @GetMapping
    public List<BookingDto> getByBookerId(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                          @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getByBookerId(bookerId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getByOwnerId(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                         @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getByOwnerId(ownerId, state);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        bookingService.delete(id);
    }
}