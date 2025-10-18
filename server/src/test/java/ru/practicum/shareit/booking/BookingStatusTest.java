package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingStatus;

import static org.junit.jupiter.api.Assertions.*;

class BookingStatusTest {

    @Test
    void shouldContainAllExpectedValues() {
        // Проверяем, что enum содержит все ожидаемые значения
        BookingStatus[] values = BookingStatus.values();

        assertEquals(4, values.length);
        assertArrayEquals(new BookingStatus[]{
                BookingStatus.WAITING,
                BookingStatus.APPROVED,
                BookingStatus.REJECTED,
                BookingStatus.CANCELED
        }, values);
    }

    @Test
    void shouldReturnCorrectStringRepresentation() {
        // Проверяем строковое представление enum значений
        assertEquals("WAITING", BookingStatus.WAITING.name());
        assertEquals("APPROVED", BookingStatus.APPROVED.name());
        assertEquals("REJECTED", BookingStatus.REJECTED.name());
        assertEquals("CANCELED", BookingStatus.CANCELED.name());
    }

    @Test
    void shouldConvertFromString() {
        // Проверяем преобразование из строки
        assertEquals(BookingStatus.WAITING, BookingStatus.valueOf("WAITING"));
        assertEquals(BookingStatus.APPROVED, BookingStatus.valueOf("APPROVED"));
        assertEquals(BookingStatus.REJECTED, BookingStatus.valueOf("REJECTED"));
        assertEquals(BookingStatus.CANCELED, BookingStatus.valueOf("CANCELED"));
    }

}