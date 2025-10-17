package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingWithUserDto;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private BookingService bookingService;


    @Test
    void create_WhenUserNotFound_ShouldThrowException() {
        // Given
        Long bookerId = 999L;
        BookingCreateDto createDto = new BookingCreateDto();
        createDto.setItemId(1L);
        createDto.setStart(LocalDateTime.now().plusDays(1));
        createDto.setEnd(LocalDateTime.now().plusDays(2));

        when(userRepository.findById(bookerId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () -> bookingService.create(createDto, bookerId));

        verify(userRepository).findById(bookerId);
        verifyNoInteractions(itemRepository, bookingRepository);
    }

    @Test
    void create_WhenItemNotFound_ShouldThrowException() {
        // Given
        Long bookerId = 2L;
        Long itemId = 999L;

        User booker = new User();
        booker.setId(bookerId);

        BookingCreateDto createDto = new BookingCreateDto();
        createDto.setItemId(itemId);
        createDto.setStart(LocalDateTime.now().plusDays(1));
        createDto.setEnd(LocalDateTime.now().plusDays(2));

        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () -> bookingService.create(createDto, bookerId));

        verify(userRepository).findById(bookerId);
        verify(itemRepository).findById(itemId);
        verifyNoInteractions(bookingRepository);
    }

    @Test
    void create_WhenItemNotAvailable_ShouldThrowException() {
        // Given
        Long bookerId = 2L;
        Long itemId = 1L;

        User booker = new User();
        booker.setId(bookerId);

        User owner = new User();
        owner.setId(1L);

        Item item = new Item();
        item.setId(itemId);
        item.setAvailable(false);
        item.setOwner(owner);

        BookingCreateDto createDto = new BookingCreateDto();
        createDto.setItemId(itemId);
        createDto.setStart(LocalDateTime.now().plusDays(1));
        createDto.setEnd(LocalDateTime.now().plusDays(2));

        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        // When & Then
        assertThrows(ValidationException.class, () -> bookingService.create(createDto, bookerId));

        verify(userRepository).findById(bookerId);
        verify(itemRepository).findById(itemId);
        verifyNoInteractions(bookingRepository);
    }

    @Test
    void create_WhenOwnerBooksOwnItem_ShouldThrowException() {
        // Given
        Long ownerId = 1L;
        Long itemId = 1L;

        User owner = new User();
        owner.setId(ownerId);

        Item item = new Item();
        item.setId(itemId);
        item.setAvailable(true);
        item.setOwner(owner);

        BookingCreateDto createDto = new BookingCreateDto();
        createDto.setItemId(itemId);
        createDto.setStart(LocalDateTime.now().plusDays(1));
        createDto.setEnd(LocalDateTime.now().plusDays(2));

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        // When & Then
        assertThrows(AccessDeniedException.class, () -> bookingService.create(createDto, ownerId));

        verify(userRepository).findById(ownerId);
        verify(itemRepository).findById(itemId);
        verifyNoInteractions(bookingRepository);
    }

    @Test
    void create_WithInvalidDates_ShouldThrowException() {
        // Given
        Long bookerId = 2L;
        Long itemId = 1L;

        User booker = new User();
        booker.setId(bookerId);

        User owner = new User();
        owner.setId(1L);

        Item item = new Item();
        item.setId(itemId);
        item.setAvailable(true);
        item.setOwner(owner);

        BookingCreateDto createDto = new BookingCreateDto();
        createDto.setItemId(itemId);
        createDto.setStart(LocalDateTime.now().minusDays(1)); // Past date - invalid
        createDto.setEnd(LocalDateTime.now().plusDays(2));

        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        // When & Then
        assertThrows(ValidationException.class, () -> bookingService.create(createDto, bookerId));

        verify(userRepository).findById(bookerId);
        verify(itemRepository).findById(itemId);
        verifyNoInteractions(bookingRepository);
    }


    @Test
    void getById_WhenBookingNotFound_ShouldThrowException() {
        // Given
        Long bookingId = 999L;
        Long userId = 1L;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () -> bookingService.getById(bookingId, userId));

        verify(bookingRepository).findById(bookingId);
        verifyNoInteractions(bookingMapper);
    }

    @Test
    void getById_WhenUserHasNoAccess_ShouldThrowException() {
        // Given
        Long bookingId = 1L;
        Long userId = 999L; // User who is neither booker nor owner

        User booker = new User();
        booker.setId(2L);

        User owner = new User();
        owner.setId(3L);

        Item item = new Item();
        item.setId(1L);
        item.setOwner(owner);

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setBooker(booker);
        booking.setItem(item);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        // When & Then
        assertThrows(AccessDeniedException.class, () -> bookingService.getById(bookingId, userId));

        verify(bookingRepository).findById(bookingId);
        verifyNoInteractions(bookingMapper);
    }


    @Test
    void updateStatus_WhenNotOwner_ShouldThrowException() {
        // Given
        Long bookingId = 1L;
        Long notOwnerId = 999L;
        Boolean approved = true;

        User owner = new User();
        owner.setId(1L);

        User booker = new User();
        booker.setId(2L);

        Item item = new Item();
        item.setId(1L);
        item.setOwner(owner);

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setItem(item);
        booking.setBooker(booker);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        // When & Then
        assertThrows(AccessDeniedException.class, () ->
                bookingService.updateStatus(bookingId, approved, notOwnerId));

        verify(bookingRepository).findById(bookingId);
        verify(bookingRepository, never()).save(any());
    }


    @Test
    void getByBookerId_WithUnknownState_ShouldThrowException() {
        // Given
        Long bookerId = 1L;
        String state = "UNKNOWN";

        User booker = new User();
        booker.setId(bookerId);

        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));

        // When & Then
        assertThrows(ValidationException.class, () -> bookingService.getByBookerId(bookerId, state));

        verify(userRepository).findById(bookerId);
        verifyNoInteractions(bookingRepository);
    }

    @Test
    void delete_ShouldDeleteBooking() {
        // Given
        Long bookingId = 1L;

        when(bookingRepository.existsById(bookingId)).thenReturn(true);

        // When
        bookingService.delete(bookingId);

        // Then
        verify(bookingRepository).existsById(bookingId);
        verify(bookingRepository).deleteById(bookingId);
    }

    @Test
    void delete_WhenBookingNotFound_ShouldThrowException() {
        // Given
        Long bookingId = 999L;

        when(bookingRepository.existsById(bookingId)).thenReturn(false);

        // When & Then
        assertThrows(NotFoundException.class, () -> bookingService.delete(bookingId));

        verify(bookingRepository).existsById(bookingId);
        verify(bookingRepository, never()).deleteById(bookingId);
    }

    @Test
    void updateStatus_WhenApproved_ShouldUpdateToApproved() {
        // Given
        Long bookingId = 1L;
        Long ownerId = 1L;

        User owner = new User();
        owner.setId(ownerId);

        User booker = new User();
        booker.setId(2L);

        Item item = new Item();
        item.setId(1L);
        item.setOwner(owner);

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        // When
        BookingDto result = bookingService.updateStatus(bookingId, true, ownerId);

        // Then
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.APPROVED);
        verify(bookingRepository).save(booking);
    }

    @Test
    void getByBookerId_WithAllStates_ShouldReturnCorrectBookings() {
        // Given
        Long bookerId = 1L;
        User booker = new User();
        booker.setId(bookerId);

        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerIdOrderByStartDesc(bookerId)).thenReturn(List.of());
        when(bookingRepository.findCurrentBookingsByBookerId(eq(bookerId), any(LocalDateTime.class))).thenReturn(List.of());
        // ... аналогично для других states

        // When & Then - проверяем все состояния
        assertDoesNotThrow(() -> bookingService.getByBookerId(bookerId, "ALL"));
        assertDoesNotThrow(() -> bookingService.getByBookerId(bookerId, "CURRENT"));
        assertDoesNotThrow(() -> bookingService.getByBookerId(bookerId, "PAST"));
        assertDoesNotThrow(() -> bookingService.getByBookerId(bookerId, "FUTURE"));
        assertDoesNotThrow(() -> bookingService.getByBookerId(bookerId, "WAITING"));
        assertDoesNotThrow(() -> bookingService.getByBookerId(bookerId, "REJECTED"));
    }

    @Test
    void getByOwnerId_ShouldReturnOwnerBookings() {
        // Given
        Long ownerId = 1L;
        User owner = new User();
        owner.setId(ownerId);

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(bookingRepository.findByItemOwnerIdOrderByStartDesc(ownerId)).thenReturn(List.of());

        // When
        List<BookingWithUserDto> result = bookingService.getByOwnerId(ownerId, "ALL");

        // Then
        assertNotNull(result);
        verify(bookingRepository).findByItemOwnerIdOrderByStartDesc(ownerId);
    }

    @Test
    void getById_WhenUserIsBooker_ShouldReturnBooking() {
        // Given
        Long bookingId = 1L;
        Long bookerId = 2L;

        User booker = new User();
        booker.setId(bookerId);

        User owner = new User();
        owner.setId(1L);

        Item item = new Item();
        item.setId(1L);
        item.setOwner(owner);

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        // When
        BookingDto result = bookingService.getById(bookingId, bookerId);

        // Then
        assertNotNull(result);
        assertEquals(bookingId, result.getId());
    }

    @Test
    void create_WhenOverlappingBookingsExist_ShouldThrowException() {
        // Given
        Long bookerId = 2L;
        Long itemId = 1L;

        User booker = new User();
        booker.setId(bookerId);

        User owner = new User();
        owner.setId(1L);

        Item item = new Item();
        item.setId(itemId);
        item.setAvailable(true);
        item.setOwner(owner);

        BookingCreateDto createDto = new BookingCreateDto();
        createDto.setItemId(itemId);
        createDto.setStart(LocalDateTime.now().plusDays(1));
        createDto.setEnd(LocalDateTime.now().plusDays(2));

        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.existOverlappingBookings(eq(itemId), any(LocalDateTime.class),
                any(LocalDateTime.class), isNull())).thenReturn(true);

        // When & Then
        assertThrows(ValidationException.class, () -> bookingService.create(createDto, bookerId));
    }
}