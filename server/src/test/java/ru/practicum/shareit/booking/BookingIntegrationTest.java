package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class BookingIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @Test
    void createBooking_ShouldCreateBookingSuccessfully() {
        // Создаем владельца
        UserDto owner = createUser("owner@mail.com", "Owner");

        // Создаем бронирующего
        UserDto booker = createUser("booker@mail.com", "Booker");

        // Создаем вещь
        ItemDto item = createItem("Item", "Description", true, owner.getId());

        // Создаем бронирование
        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setItemId(item.getId());
        bookingCreateDto.setStart(LocalDateTime.now().plusDays(1));
        bookingCreateDto.setEnd(LocalDateTime.now().plusDays(2));

        // Выполняем создание бронирования
        BookingDto result = bookingService.create(bookingCreateDto, booker.getId());

        // Проверяем результат
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(booker.getId(), result.getBooker().getId());
        assertEquals(item.getId(), result.getItem().getId());
    }

    @Test
    void createBooking_WhenItemNotAvailable_ShouldThrowException() {
        UserDto owner = createUser("owner2@mail.com", "Owner");
        UserDto booker = createUser("booker2@mail.com", "Booker");
        ItemDto item = createItem("Item2", "Description", false, owner.getId());

        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setItemId(item.getId());
        bookingCreateDto.setStart(LocalDateTime.now().plusDays(1));
        bookingCreateDto.setEnd(LocalDateTime.now().plusDays(2));

        assertThrows(ValidationException.class, () ->
                bookingService.create(bookingCreateDto, booker.getId()));
    }

    private UserDto createUser(String email, String name) {
        UserDto userDto = new UserDto();
        userDto.setEmail(email);
        userDto.setName(name);
        return userService.createUser(userDto);
    }

    private ItemDto createItem(String name, String description, Boolean available, Long ownerId) {
        ItemDto itemDto = new ItemDto();
        itemDto.setName(name);
        itemDto.setDescription(description);
        itemDto.setAvailable(available);
        return itemService.create(itemDto, ownerId);
    }
}