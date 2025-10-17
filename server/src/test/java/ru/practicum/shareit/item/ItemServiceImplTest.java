package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.item.dto.BookingInfoDto;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import static org.assertj.core.api.Assertions.assertThat;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void create_ShouldCreateItemSuccessfully() {
        // Given
        Long ownerId = 1L;
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Item");
        itemDto.setDescription("Description");
        itemDto.setAvailable(true);

        User owner = new User();
        owner.setId(ownerId);
        owner.setName("Owner");

        Item item = new Item();
        item.setId(1L);
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(owner);

        ItemDto expectedDto = new ItemDto();
        expectedDto.setId(1L);
        expectedDto.setName("Item");
        expectedDto.setDescription("Description");
        expectedDto.setAvailable(true);

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(itemMapper.toItem(itemDto, owner)).thenReturn(item);
        when(itemRepository.save(item)).thenReturn(item);
        when(itemMapper.toItemDto(item)).thenReturn(expectedDto);

        // When
        ItemDto result = itemService.create(itemDto, ownerId);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Item", result.getName());
        assertEquals("Description", result.getDescription());
        assertTrue(result.getAvailable());

        verify(userRepository).findById(ownerId);
        verify(itemMapper).toItem(itemDto, owner);
        verify(itemRepository).save(item);
        verify(itemMapper).toItemDto(item);
    }

    @Test
    void create_WithRequestId_ShouldCreateItemWithRequest() {
        // Given
        Long ownerId = 1L;
        Long requestId = 1L;

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Item");
        itemDto.setDescription("Description");
        itemDto.setAvailable(true);
        itemDto.setRequestId(requestId);

        User owner = new User();
        owner.setId(ownerId);

        ItemRequest request = new ItemRequest();
        request.setId(requestId);

        Item item = new Item();
        item.setId(1L);
        item.setRequest(request);

        ItemDto expectedDto = new ItemDto();
        expectedDto.setId(1L);

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(itemMapper.toItem(itemDto, owner)).thenReturn(item);
        when(itemRepository.save(item)).thenReturn(item);
        when(itemMapper.toItemDto(item)).thenReturn(expectedDto);

        // When
        ItemDto result = itemService.create(itemDto, ownerId);

        // Then
        assertNotNull(result);
        verify(itemRequestRepository).findById(requestId);
        verify(itemRepository).save(item);
    }

    @Test
    void create_WhenUserNotFound_ShouldThrowException() {
        // Given
        Long ownerId = 999L;
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Item");

        when(userRepository.findById(ownerId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () -> itemService.create(itemDto, ownerId));

        verify(userRepository).findById(ownerId);
        verifyNoInteractions(itemRepository, itemMapper);
    }

    @Test
    void getById_ShouldReturnItemWithComments() {
        // Given
        Long itemId = 1L;
        Long userId = 1L;

        User owner = new User();
        owner.setId(userId);

        Item item = new Item();
        item.setId(itemId);
        item.setOwner(owner);

        ItemDto itemDto = new ItemDto();
        itemDto.setId(itemId);

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Great item!");

        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("Great item!");

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemMapper.toItemDto(item)).thenReturn(itemDto);
        when(commentRepository.findByItemIdOrderByCreatedDesc(itemId)).thenReturn(List.of(comment));
        when(commentMapper.toCommentDto(comment)).thenReturn(commentDto);

        // When
        ItemDto result = itemService.getById(itemId, userId);

        // Then
        assertNotNull(result);
        assertEquals(itemId, result.getId());
        assertNotNull(result.getComments());
        assertEquals(1, result.getComments().size());
        assertEquals("Great item!", result.getComments().get(0).getText());

        verify(itemRepository).findById(itemId);
        verify(itemMapper).toItemDto(item);
        verify(commentRepository).findByItemIdOrderByCreatedDesc(itemId);
        verify(commentMapper).toCommentDto(comment);
    }

    @Test
    void getById_WhenItemNotFound_ShouldThrowException() {
        // Given
        Long itemId = 999L;
        Long userId = 1L;

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () -> itemService.getById(itemId, userId));

        verify(itemRepository).findById(itemId);
        verifyNoInteractions(itemMapper, commentRepository);
    }


    @Test
    void update_ShouldUpdateItemSuccessfully() {
        // Given
        Long itemId = 1L;
        Long ownerId = 1L;

        User owner = new User();
        owner.setId(ownerId);

        Item existingItem = new Item();
        existingItem.setId(itemId);
        existingItem.setName("Old Name");
        existingItem.setDescription("Old Description");
        existingItem.setAvailable(true);
        existingItem.setOwner(owner);

        ItemDto updateDto = new ItemDto();
        updateDto.setName("New Name");
        updateDto.setDescription("New Description");
        updateDto.setAvailable(false);

        Item updatedItem = new Item();
        updatedItem.setId(itemId);
        updatedItem.setName("New Name");
        updatedItem.setDescription("New Description");
        updatedItem.setAvailable(false);

        ItemDto expectedDto = new ItemDto();
        expectedDto.setId(itemId);
        expectedDto.setName("New Name");

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));
        when(itemRepository.save(existingItem)).thenReturn(updatedItem);
        when(itemMapper.toItemDto(updatedItem)).thenReturn(expectedDto);

        // When
        ItemDto result = itemService.update(itemId, updateDto, ownerId);

        // Then
        assertNotNull(result);
        assertEquals("New Name", result.getName());

        verify(itemRepository).findById(itemId);
        verify(itemRepository).save(existingItem);
        verify(itemMapper).toItemDto(updatedItem);
    }

    @Test
    void update_WhenNotOwner_ShouldThrowException() {
        // Given
        Long itemId = 1L;
        Long ownerId = 1L;
        Long notOwnerId = 2L;

        User owner = new User();
        owner.setId(ownerId);

        Item existingItem = new Item();
        existingItem.setId(itemId);
        existingItem.setOwner(owner);

        ItemDto updateDto = new ItemDto();
        updateDto.setName("New Name");

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));

        // When & Then
        assertThrows(NotFoundException.class, () -> itemService.update(itemId, updateDto, notOwnerId));

        verify(itemRepository).findById(itemId);
        verify(itemRepository, never()).save(any());
    }

    @Test
    void update_WithPartialUpdate_ShouldUpdateOnlyProvidedFields() {
        // Given
        Long itemId = 1L;
        Long ownerId = 1L;

        User owner = new User();
        owner.setId(ownerId);

        Item existingItem = new Item();
        existingItem.setId(itemId);
        existingItem.setName("Old Name");
        existingItem.setDescription("Old Description");
        existingItem.setAvailable(true);
        existingItem.setOwner(owner);

        ItemDto updateDto = new ItemDto();
        updateDto.setName("New Name"); // Only update name

        Item updatedItem = new Item();
        updatedItem.setId(itemId);
        updatedItem.setName("New Name");
        updatedItem.setDescription("Old Description"); // Description remains
        updatedItem.setAvailable(true); // Available remains

        ItemDto expectedDto = new ItemDto();
        expectedDto.setId(itemId);
        expectedDto.setName("New Name");

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));
        when(itemRepository.save(existingItem)).thenReturn(updatedItem);
        when(itemMapper.toItemDto(updatedItem)).thenReturn(expectedDto);

        // When
        ItemDto result = itemService.update(itemId, updateDto, ownerId);

        // Then
        assertNotNull(result);
        assertEquals("New Name", result.getName());

        verify(itemRepository).findById(itemId);
        verify(itemRepository).save(existingItem);
    }

    @Test
    void delete_ShouldDeleteItem() {
        // Given
        Long itemId = 1L;

        // When
        itemService.delete(itemId);

        // Then
        verify(itemRepository).deleteById(itemId);
    }

    @Test
    void search_WithValidText_ShouldReturnAvailableItems() {
        // Given
        String searchText = "drill";

        Item item = new Item();
        item.setId(1L);
        item.setName("Power Drill");
        item.setDescription("Heavy duty drill");
        item.setAvailable(true);

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Power Drill");

        when(itemRepository.searchAvailableItems(searchText)).thenReturn(List.of(item));
        when(itemMapper.toItemDto(item)).thenReturn(itemDto);

        // When
        List<ItemDto> result = itemService.search(searchText);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Power Drill", result.get(0).getName());

        verify(itemRepository).searchAvailableItems(searchText);
        verify(itemMapper).toItemDto(item);
    }

    @Test
    void search_WithBlankText_ShouldReturnEmptyList() {
        // Given
        String searchText = "   ";

        // When
        List<ItemDto> result = itemService.search(searchText);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(itemRepository, never()).searchAvailableItems(anyString());
    }

    @Test
    void search_WithNullText_ShouldReturnEmptyList() {
        // When
        List<ItemDto> result = itemService.search(null);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(itemRepository, never()).searchAvailableItems(anyString());
    }

    @Test
    void addComment_ShouldAddCommentSuccessfully() {
        // Given
        Long itemId = 1L;
        Long userId = 2L;

        User author = new User();
        author.setId(userId);
        author.setName("Author");

        Item item = new Item();
        item.setId(itemId);

        CommentDto commentDto = new CommentDto();
        commentDto.setText("Great item!");

        Comment comment = new Comment();
        comment.setText("Great item!");

        Comment savedComment = new Comment();
        savedComment.setId(1L);
        savedComment.setText("Great item!");
        savedComment.setAuthor(author);
        savedComment.setItem(item);
        savedComment.setCreated(LocalDateTime.now());

        CommentDto expectedDto = new CommentDto();
        expectedDto.setId(1L);
        expectedDto.setText("Great item!");

        when(userRepository.findById(userId)).thenReturn(Optional.of(author));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.findFirstByItemIdAndBookerIdAndStatusAndEndBefore(
                eq(itemId), eq(userId), eq(BookingStatus.APPROVED), any(LocalDateTime.class)))
                .thenReturn(Optional.of(new Booking()));
        when(commentRepository.existsByAuthorIdAndItemId(userId, itemId)).thenReturn(false);
        when(commentMapper.toComment(commentDto)).thenReturn(comment);
        when(commentRepository.save(comment)).thenReturn(savedComment);
        when(commentMapper.toCommentDto(savedComment)).thenReturn(expectedDto);

        // When
        CommentDto result = itemService.addComment(itemId, commentDto, userId);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Great item!", result.getText());

        verify(userRepository).findById(userId);
        verify(itemRepository).findById(itemId);
        verify(bookingRepository).findFirstByItemIdAndBookerIdAndStatusAndEndBefore(
                eq(itemId), eq(userId), eq(BookingStatus.APPROVED), any(LocalDateTime.class));
        verify(commentRepository).existsByAuthorIdAndItemId(userId, itemId);
        verify(commentRepository).save(comment);
        verify(commentMapper).toCommentDto(savedComment);
    }

    @Test
    void addComment_WhenUserNotBookedItem_ShouldThrowException() {
        // Given
        Long itemId = 1L;
        Long userId = 2L;

        User author = new User();
        author.setId(userId);

        Item item = new Item();
        item.setId(itemId);

        CommentDto commentDto = new CommentDto();
        commentDto.setText("Great item!");

        when(userRepository.findById(userId)).thenReturn(Optional.of(author));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.findFirstByItemIdAndBookerIdAndStatusAndEndBefore(
                eq(itemId), eq(userId), eq(BookingStatus.APPROVED), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(ValidationException.class, () -> itemService.addComment(itemId, commentDto, userId));

        verify(userRepository).findById(userId);
        verify(itemRepository).findById(itemId);
        verify(bookingRepository).findFirstByItemIdAndBookerIdAndStatusAndEndBefore(
                eq(itemId), eq(userId), eq(BookingStatus.APPROVED), any(LocalDateTime.class));
        verify(commentRepository, never()).save(any());
    }

    @Test
    void addComment_WhenUserAlreadyCommented_ShouldThrowException() {
        // Given
        Long itemId = 1L;
        Long userId = 2L;

        User author = new User();
        author.setId(userId);

        Item item = new Item();
        item.setId(itemId);

        CommentDto commentDto = new CommentDto();
        commentDto.setText("Great item!");

        when(userRepository.findById(userId)).thenReturn(Optional.of(author));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.findFirstByItemIdAndBookerIdAndStatusAndEndBefore(
                eq(itemId), eq(userId), eq(BookingStatus.APPROVED), any(LocalDateTime.class)))
                .thenReturn(Optional.of(new Booking()));
        when(commentRepository.existsByAuthorIdAndItemId(userId, itemId)).thenReturn(true);

        // When & Then
        assertThrows(ValidationException.class, () -> itemService.addComment(itemId, commentDto, userId));

        verify(userRepository).findById(userId);
        verify(itemRepository).findById(itemId);
        verify(bookingRepository).findFirstByItemIdAndBookerIdAndStatusAndEndBefore(
                eq(itemId), eq(userId), eq(BookingStatus.APPROVED), any(LocalDateTime.class));
        verify(commentRepository).existsByAuthorIdAndItemId(userId, itemId);
        verify(commentRepository, never()).save(any());
    }


    @Test
    void findLastBooking_ShouldReturnLastBooking() {
        // Given
        Long itemId = 1L;
        User owner = new User();
        owner.setId(1L);

        User booker = new User();
        booker.setId(2L);

        Item item = new Item();
        item.setId(itemId);
        item.setOwner(owner);

        Booking lastBooking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().minusDays(3))
                .end(LocalDateTime.now().minusDays(1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();

        when(bookingRepository.findCompletedBookingsByItemId(eq(itemId), any(LocalDateTime.class)))
                .thenReturn(List.of(lastBooking));

        // When
        BookingInfoDto result = itemService.findLastBooking(itemId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getBookerId()).isEqualTo(2L);
        verify(bookingRepository).findCompletedBookingsByItemId(eq(itemId), any(LocalDateTime.class));
    }

    @Test
    void findLastBooking_WhenNoBookings_ShouldReturnNull() {
        // Given
        Long itemId = 1L;
        when(bookingRepository.findCompletedBookingsByItemId(eq(itemId), any(LocalDateTime.class)))
                .thenReturn(List.of());

        // When
        BookingInfoDto result = itemService.findLastBooking(itemId);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void findNextBooking_ShouldReturnNextBooking() {
        // Given
        Long itemId = 1L;
        User owner = new User();
        owner.setId(1L);

        User booker = new User();
        booker.setId(2L);

        Item item = new Item();
        item.setId(itemId);
        item.setOwner(owner);

        Booking nextBooking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();

        when(bookingRepository.findFutureBookingsByItemId(eq(itemId), any(LocalDateTime.class)))
                .thenReturn(List.of(nextBooking));

        // When
        BookingInfoDto result = itemService.findNextBooking(itemId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getBookerId()).isEqualTo(2L);
        verify(bookingRepository).findFutureBookingsByItemId(eq(itemId), any(LocalDateTime.class));
    }

    @Test
    void findNextBooking_WhenNoBookings_ShouldReturnNull() {
        // Given
        Long itemId = 1L;
        when(bookingRepository.findFutureBookingsByItemId(eq(itemId), any(LocalDateTime.class)))
                .thenReturn(List.of());

        // When
        BookingInfoDto result = itemService.findNextBooking(itemId);

        // Then
        assertThat(result).isNull();
    }

    // Дополнительные edge cases
    @Test
    void getById_WhenUserIsOwner_ShouldIncludeBookings() {
        // Given
        Long itemId = 1L;
        Long ownerId = 1L;

        User owner = new User();
        owner.setId(ownerId);

        Item item = new Item();
        item.setId(itemId);
        item.setOwner(owner);

        // Создаем объект ItemDto
        ItemDto itemDto = new ItemDto();
        itemDto.setId(itemId);
        itemDto.setName("Test Item");

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemMapper.toItemDto(item)).thenReturn(itemDto);
        when(bookingRepository.findCompletedBookingsByItemId(eq(itemId), any(LocalDateTime.class)))
                .thenReturn(List.of());
        when(bookingRepository.findFutureBookingsByItemId(eq(itemId), any(LocalDateTime.class)))
                .thenReturn(List.of());

        // When
        ItemDto result = itemService.getById(itemId, ownerId);

        // Then
        assertNotNull(result);
        // Проверяем что методы поиска бронирований были вызваны для владельца
        verify(bookingRepository).findCompletedBookingsByItemId(eq(itemId), any(LocalDateTime.class));
        verify(bookingRepository).findFutureBookingsByItemId(eq(itemId), any(LocalDateTime.class));
    }

    @Test
    void getById_WhenUserIsNotOwner_ShouldNotIncludeBookings() {
        // Given
        Long itemId = 1L;
        Long ownerId = 1L;
        Long otherUserId = 2L;

        User owner = new User();
        owner.setId(ownerId);

        Item item = new Item();
        item.setId(itemId);
        item.setOwner(owner);

        // Создаем объект ItemDto
        ItemDto itemDto = new ItemDto();
        itemDto.setId(itemId);
        itemDto.setName("Test Item");

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemMapper.toItemDto(item)).thenReturn(itemDto);

        // When
        ItemDto result = itemService.getById(itemId, otherUserId);

        // Then
        assertNotNull(result);
        // Проверяем что методы поиска бронирований НЕ были вызваны для не-владельца
        verify(bookingRepository, never()).findCompletedBookingsByItemId(anyLong(), any(LocalDateTime.class));
        verify(bookingRepository, never()).findFutureBookingsByItemId(anyLong(), any(LocalDateTime.class));
    }

}