package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import ru.practicum.shareit.booking.dto.BookingStatus;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class BookingStatusTest {

    @Test
    @DisplayName("Должен содержать все ожидаемые статусы")
    void shouldContainAllExpectedStatuses() {
        BookingStatus[] statuses = BookingStatus.values();

        assertEquals(4, statuses.length);
        assertArrayEquals(new BookingStatus[]{
                BookingStatus.WAITING,
                BookingStatus.APPROVED,
                BookingStatus.REJECTED,
                BookingStatus.CANCELED
        }, statuses);
    }

    @ParameterizedTest
    @EnumSource(BookingStatus.class)
    @DisplayName("Должен корректно возвращать статус по имени")
    void shouldReturnStatusByName(BookingStatus status) {
        String name = status.name();
        BookingStatus foundStatus = BookingStatus.valueOf(name);

        assertEquals(status, foundStatus);
    }

    @Test
    @DisplayName("Должен иметь правильные порядковые номера")
    void shouldHaveCorrectOrdinalValues() {
        assertEquals(0, BookingStatus.WAITING.ordinal());
        assertEquals(1, BookingStatus.APPROVED.ordinal());
        assertEquals(2, BookingStatus.REJECTED.ordinal());
        assertEquals(3, BookingStatus.CANCELED.ordinal());
    }

    @Test
    @DisplayName("Должен корректно преобразовываться в строку")
    void shouldConvertToStringCorrectly() {
        assertEquals("WAITING", BookingStatus.WAITING.toString());
        assertEquals("APPROVED", BookingStatus.APPROVED.toString());
        assertEquals("REJECTED", BookingStatus.REJECTED.toString());
        assertEquals("CANCELED", BookingStatus.CANCELED.toString());
    }

    @Test
    @DisplayName("Должен корректно работать с valueOf для всех значений")
    void shouldWorkWithValueOfForAllValues() {
        assertEquals(BookingStatus.WAITING, BookingStatus.valueOf("WAITING"));
        assertEquals(BookingStatus.APPROVED, BookingStatus.valueOf("APPROVED"));
        assertEquals(BookingStatus.REJECTED, BookingStatus.valueOf("REJECTED"));
        assertEquals(BookingStatus.CANCELED, BookingStatus.valueOf("CANCELED"));
    }

    @Test
    @DisplayName("Должен бросать исключение при попытке получить несуществующий статус")
    void shouldThrowExceptionForInvalidStatusName() {
        assertThrows(IllegalArgumentException.class, () -> {
            BookingStatus.valueOf("INVALID_STATUS");
        });
    }

    @Test
    @DisplayName("Должен корректно работать с values() методом")
    void shouldWorkWithValuesMethod() {
        BookingStatus[] values = BookingStatus.values();

        assertNotNull(values);
        assertEquals(4, values.length);
        assertTrue(values.length > 0);
    }

    @Test
    @DisplayName("Должен корректно работать в switch выражениях")
    void shouldWorkInSwitchExpressions() {
        String waitingDescription = getStatusDescription(BookingStatus.WAITING);
        String approvedDescription = getStatusDescription(BookingStatus.APPROVED);
        String rejectedDescription = getStatusDescription(BookingStatus.REJECTED);
        String canceledDescription = getStatusDescription(BookingStatus.CANCELED);

        assertEquals("Ожидает подтверждения", waitingDescription);
        assertEquals("Подтверждено", approvedDescription);
        assertEquals("Отклонено", rejectedDescription);
        assertEquals("Отменено", canceledDescription);
    }

    private String getStatusDescription(BookingStatus status) {
        return switch (status) {
            case WAITING -> "Ожидает подтверждения";
            case APPROVED -> "Подтверждено";
            case REJECTED -> "Отклонено";
            case CANCELED -> "Отменено";
        };
    }

    @Test
    @DisplayName("Должен корректно сравниваться через equals")
    void shouldCompareCorrectlyWithEquals() {
        // Проверка рефлексивности
        assertEquals(BookingStatus.WAITING, BookingStatus.WAITING);

        // Проверка симметричности
        BookingStatus status1 = BookingStatus.APPROVED;
        BookingStatus status2 = BookingStatus.APPROVED;
        assertEquals(status1, status2);
        assertEquals(status2, status1);

        // Проверка неравенства
        assertNotEquals(BookingStatus.WAITING, BookingStatus.APPROVED);
        assertNotEquals(BookingStatus.APPROVED, BookingStatus.REJECTED);
    }

    @Test
    @DisplayName("Должен иметь корректные хэш-коды")
    void shouldHaveCorrectHashCodes() {
        // Одинаковые статусы должны иметь одинаковые хэш-коды
        assertEquals(BookingStatus.WAITING.hashCode(), BookingStatus.WAITING.hashCode());
        assertEquals(BookingStatus.APPROVED.hashCode(), BookingStatus.APPROVED.hashCode());

        // Разные статусы обычно имеют разные хэш-коды (но это не гарантировано)
        // Проверяем хотя бы что они не бросают исключений
        assertDoesNotThrow(() -> BookingStatus.WAITING.hashCode());
        assertDoesNotThrow(() -> BookingStatus.APPROVED.hashCode());
        assertDoesNotThrow(() -> BookingStatus.REJECTED.hashCode());
        assertDoesNotThrow(() -> BookingStatus.CANCELED.hashCode());
    }

    @Test
    @DisplayName("Должен корректно работать в коллекциях")
    void shouldWorkCorrectlyInCollections() {
        // Используем HashSet вместо Set.of() чтобы избежать NPE при проверке contains(null)
        Set<BookingStatus> statusSet = new HashSet<>();
        statusSet.add(BookingStatus.WAITING);
        statusSet.add(BookingStatus.APPROVED);
        statusSet.add(BookingStatus.REJECTED);
        statusSet.add(BookingStatus.CANCELED);

        assertEquals(4, statusSet.size());
        assertTrue(statusSet.contains(BookingStatus.WAITING));
        assertTrue(statusSet.contains(BookingStatus.APPROVED));
        assertTrue(statusSet.contains(BookingStatus.REJECTED));
        assertTrue(statusSet.contains(BookingStatus.CANCELED));
        assertFalse(statusSet.contains(null)); // Теперь это безопасно
    }

    @Test
    @DisplayName("Должен корректно сериализоваться в JSON")
    void shouldSerializeToJsonCorrectly() {
        // Проверяем, что имена статусов соответствуют ожидаемым для JSON сериализации
        assertEquals("WAITING", BookingStatus.WAITING.name());
        assertEquals("APPROVED", BookingStatus.APPROVED.name());
        assertEquals("REJECTED", BookingStatus.REJECTED.name());
        assertEquals("CANCELED", BookingStatus.CANCELED.name());
    }

    @Test
    @DisplayName("Должен иметь логичную последовательность статусов")
    void shouldHaveLogicalStatusSequence() {
        BookingStatus[] statuses = BookingStatus.values();

        // Проверяем логичный порядок: ожидание -> подтверждение/отклонение/отмена
        assertEquals(BookingStatus.WAITING, statuses[0]);

        // Проверяем, что все статусы уникальны
        Set<BookingStatus> uniqueStatuses = new HashSet<>();
        for (BookingStatus status : statuses) {
            uniqueStatuses.add(status);
        }
        assertEquals(statuses.length, uniqueStatuses.size());
    }
}