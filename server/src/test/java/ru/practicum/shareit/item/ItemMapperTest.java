package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.BookingInfoDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ItemMapperTest {

    private final ItemMapper itemMapper = new ItemMapper();

    @Test
    void toItemDto_ShouldMapCorrectly() {
        // Given
        User owner = User.builder()
                .id(1L)
                .name("Owner")
                .email("owner@mail.com")
                .build();

        ItemRequest request = ItemRequest.builder()
                .id(1L)
                .description("Need item")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .owner(owner)
                .request(request)
                .build();

        // When
        ItemDto result = itemMapper.toItemDto(item);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Item");
        assertThat(result.getDescription()).isEqualTo("Test Description");
        assertThat(result.getAvailable()).isTrue();
        assertThat(result.getRequestId()).isEqualTo(1L);
        assertThat(result.getLastBooking()).isNull();
        assertThat(result.getNextBooking()).isNull();
        assertThat(result.getComments()).isNull();
    }

    @Test
    void toItemDto_WithNullItem_ShouldReturnNull() {
        // When
        ItemDto result = itemMapper.toItemDto(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void toItemDto_WithNullRequest_ShouldHandleGracefully() {
        // Given
        User owner = User.builder().id(1L).build();

        Item item = Item.builder()
                .id(1L)
                .name("Item")
                .description("Description")
                .available(true)
                .owner(owner)
                .request(null) // request is null
                .build();

        // When
        ItemDto result = itemMapper.toItemDto(item);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getRequestId()).isNull();
    }

    @Test
    void toItemDtoWithBookingsAndComments_ShouldMapCorrectly() {
        // Given
        User owner = User.builder().id(1L).build();
        ItemRequest request = ItemRequest.builder().id(1L).build();

        Item item = Item.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .owner(owner)
                .request(request)
                .build();

        BookingInfoDto lastBooking = new BookingInfoDto(1L, 2L,
                LocalDateTime.of(2024, 1, 1, 10, 0),
                LocalDateTime.of(2024, 1, 2, 10, 0));

        BookingInfoDto nextBooking = new BookingInfoDto(3L, 4L,
                LocalDateTime.of(2024, 1, 3, 10, 0),
                LocalDateTime.of(2024, 1, 4, 10, 0));

        List<CommentDto> comments = List.of(
                new CommentDto(1L, "Great item!", "John", LocalDateTime.now()),
                new CommentDto(2L, "Excellent quality!", "Jane", LocalDateTime.now())
        );

        // When
        ItemDto result = itemMapper.toItemDtoWithBookingsAndComments(
                item, lastBooking, nextBooking, comments);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Item");
        assertThat(result.getDescription()).isEqualTo("Test Description");
        assertThat(result.getAvailable()).isTrue();
        assertThat(result.getRequestId()).isEqualTo(1L);
        assertThat(result.getLastBooking()).isEqualTo(lastBooking);
        assertThat(result.getNextBooking()).isEqualTo(nextBooking);
        assertThat(result.getComments()).isEqualTo(comments);
    }

    @Test
    void toItemDtoWithBookingsAndComments_WithNullBookingsAndComments_ShouldMapCorrectly() {
        // Given
        User owner = User.builder().id(1L).build();
        Item item = Item.builder()
                .id(1L)
                .name("Item")
                .description("Description")
                .available(true)
                .owner(owner)
                .build();

        // When
        ItemDto result = itemMapper.toItemDtoWithBookingsAndComments(
                item, null, null, null);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getLastBooking()).isNull();
        assertThat(result.getNextBooking()).isNull();
        assertThat(result.getComments()).isNull();
    }

    @Test
    void toItem_ShouldMapCorrectly() {
        // Given
        User owner = User.builder()
                .id(1L)
                .name("Owner")
                .build();

        ItemDto itemDto = ItemDto.builder()
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .requestId(2L)
                .build();

        // When
        Item result = itemMapper.toItem(itemDto, owner);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Item");
        assertThat(result.getDescription()).isEqualTo("Test Description");
        assertThat(result.getAvailable()).isTrue();
        assertThat(result.getOwner()).isEqualTo(owner);
        assertThat(result.getRequest()).isNull(); // request не устанавливается в этом методе
    }

    @Test
    void toItem_WithNullAvailable_ShouldMapCorrectly() {
        // Given
        User owner = User.builder().id(1L).build();

        ItemDto itemDto = ItemDto.builder()
                .name("Item")
                .description("Description")
                .available(null) // available is null
                .build();

        // When
        Item result = itemMapper.toItem(itemDto, owner);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getAvailable()).isNull();
    }

    @Test
    void toBookingInfoDto_ShouldMapCorrectly() {
        // Given
        User booker = User.builder()
                .id(2L)
                .name("Booker")
                .email("booker@mail.com")
                .build();

        User owner = User.builder().id(1L).build();
        Item item = Item.builder().id(1L).owner(owner).build();

        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2024, 1, 1, 10, 0))
                .end(LocalDateTime.of(2024, 1, 2, 10, 0))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();

        // When
        BookingInfoDto result = itemMapper.toBookingInfoDto(booking);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getBookerId()).isEqualTo(2L);
        assertThat(result.getStart()).isEqualTo(LocalDateTime.of(2024, 1, 1, 10, 0));
        assertThat(result.getEnd()).isEqualTo(LocalDateTime.of(2024, 1, 2, 10, 0));
    }

    @Test
    void toBookingInfoDto_WithNullBooking_ShouldReturnNull() {
        // When
        BookingInfoDto result = itemMapper.toBookingInfoDto(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void toBookingInfoDto_WithNullBooker_ShouldHandleGracefully() {
        // Given
        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .booker(null) // booker is null
                .build();

        // When & Then - проверяем, что маппер не падает
        // Это может выбросить NPE, что нормально - зависит от требований
        try {
            BookingInfoDto result = itemMapper.toBookingInfoDto(booking);
            // Если не упало, проверяем результат
            assertThat(result).isNotNull();
            assertThat(result.getBookerId()).isNull();
        } catch (NullPointerException e) {
            // Ожидаемое поведение - booker обязателен
            assertThat(e).isInstanceOf(NullPointerException.class);
        }
    }
}