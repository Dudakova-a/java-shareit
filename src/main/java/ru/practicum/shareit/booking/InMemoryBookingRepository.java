package ru.practicum.shareit.booking;

import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class InMemoryBookingRepository implements BookingRepository {
    private final Map<Long, Booking> bookings = new HashMap<>();
    private Long nextId = 1L;

    @Override
    public Booking save(Booking booking) {
        if (booking.getId() == null) {
            booking.setId(nextId++);
        }
        bookings.put(booking.getId(), booking);
        return booking;
    }

    @Override
    public Optional<Booking> findById(Long id) {
        return Optional.ofNullable(bookings.get(id));
    }

    @Override
    public List<Booking> findAll() {
        return new ArrayList<>(bookings.values());
    }

    @Override
    public List<Booking> findByBookerId(Long bookerId) {
        return bookings.values().stream()
                .filter(booking -> booking.getBookerId().equals(bookerId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Booking> findByItemId(Long itemId) {
        return bookings.values().stream()
                .filter(booking -> booking.getItemId().equals(itemId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Booking> findByItemOwnerId(Long ownerId) {
        return bookings.values().stream()
                .filter(booking -> {
                    // Здесь нужно будет добавить логику определения владельца вещи
                    // через сервис items
                    return true; // заглушка
                })
                .collect(Collectors.toList());
    }

    @Override
    public Booking update(Booking booking) {
        bookings.put(booking.getId(), booking);
        return booking;
    }

    @Override
    public void deleteById(Long id) {
        bookings.remove(id);
    }
}