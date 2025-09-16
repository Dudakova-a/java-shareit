package ru.practicum.shareit.booking;

import java.util.List;
import java.util.Optional;

public interface BookingRepository {
    Booking save(Booking booking);

    Optional<Booking> findById(Long id);

    List<Booking> findAll();

    List<Booking> findByBookerId(Long bookerId);

    List<Booking> findByItemId(Long itemId);

    List<Booking> findByItemOwnerId(Long ownerId);

    Booking update(Booking booking);

    void deleteById(Long id);
}