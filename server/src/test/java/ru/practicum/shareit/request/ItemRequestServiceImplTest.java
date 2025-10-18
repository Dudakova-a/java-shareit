package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private ItemRequestMapper itemRequestMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Test
    void create_ShouldCreateItemRequestSuccessfully() {
        // Given
        Long userId = 1L;
        User requester = new User();
        requester.setId(userId);
        requester.setName("John");
        requester.setEmail("john@mail.com");

        ItemRequestCreateDto createDto = new ItemRequestCreateDto();
        createDto.setDescription("Need a drill");

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Need a drill");
        itemRequest.setRequester(requester);
        itemRequest.setCreated(LocalDateTime.now());

        ItemRequestDto expectedDto = new ItemRequestDto();
        expectedDto.setId(1L);
        expectedDto.setDescription("Need a drill");
        expectedDto.setItems(List.of());

        when(userRepository.findById(userId)).thenReturn(Optional.of(requester));
        when(itemRequestMapper.toItemRequest(createDto, requester)).thenReturn(itemRequest);
        when(itemRequestRepository.save(itemRequest)).thenReturn(itemRequest);
        when(itemRequestMapper.toItemRequestDto(itemRequest)).thenReturn(expectedDto);

        // When
        ItemRequestDto result = itemRequestService.create(createDto, userId);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Need a drill", result.getDescription());
        assertTrue(result.getItems().isEmpty());

        verify(userRepository).findById(userId);
        verify(itemRequestMapper).toItemRequest(createDto, requester);
        verify(itemRequestRepository).save(itemRequest);
        verify(itemRequestMapper).toItemRequestDto(itemRequest);
    }

    @Test
    void create_WhenUserNotFound_ShouldThrowException() {
        // Given
        Long userId = 999L;
        ItemRequestCreateDto createDto = new ItemRequestCreateDto();
        createDto.setDescription("Need a drill");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () ->
                itemRequestService.create(createDto, userId));

        verify(userRepository).findById(userId);
        verifyNoInteractions(itemRequestMapper, itemRequestRepository);
    }

    @Test
    void getById_ShouldReturnItemRequestWithItems() {
        // Given
        Long requestId = 1L;
        User requester = new User();
        requester.setId(1L);
        requester.setName("John");

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(requestId);
        itemRequest.setDescription("Need a drill");
        itemRequest.setRequester(requester);

        ItemRequestDto expectedDto = new ItemRequestDto();
        expectedDto.setId(requestId);
        expectedDto.setDescription("Need a drill");

        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));
        when(itemRequestMapper.toItemRequestDto(itemRequest)).thenReturn(expectedDto);
        when(itemRepository.findByRequestId(requestId)).thenReturn(List.of());

        // When
        ItemRequestDto result = itemRequestService.getById(requestId);

        // Then
        assertNotNull(result);
        assertEquals(requestId, result.getId());
        assertEquals("Need a drill", result.getDescription());
        assertNotNull(result.getItems());

        verify(itemRequestRepository).findById(requestId);
        verify(itemRequestMapper).toItemRequestDto(itemRequest);
        verify(itemRepository).findByRequestId(requestId);
    }

    @Test
    void getById_WhenRequestNotFound_ShouldThrowException() {
        // Given
        Long requestId = 999L;
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () ->
                itemRequestService.getById(requestId));

        verify(itemRequestRepository).findById(requestId);
        verifyNoInteractions(itemRepository);
    }


    @Test
    void getByUserId_WhenUserNotFound_ShouldThrowException() {
        // Given
        Long userId = 999L;
        when(userRepository.existsById(userId)).thenReturn(false);

        // When & Then
        assertThrows(NotFoundException.class, () ->
                itemRequestService.getByUserId(userId));

        verify(userRepository).existsById(userId);
        verifyNoInteractions(itemRequestRepository);
    }


    @Test
    void update_ShouldUpdateItemRequest() {
        // Given
        Long requestId = 1L;
        ItemRequest existingRequest = new ItemRequest();
        existingRequest.setId(requestId);
        existingRequest.setDescription("Old description");

        ItemRequestDto updateDto = new ItemRequestDto();
        updateDto.setDescription("Updated description");

        ItemRequest updatedRequest = new ItemRequest();
        updatedRequest.setId(requestId);
        updatedRequest.setDescription("Updated description");

        ItemRequestDto expectedDto = new ItemRequestDto();
        expectedDto.setId(requestId);
        expectedDto.setDescription("Updated description");

        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(existingRequest));
        when(itemRequestRepository.save(existingRequest)).thenReturn(updatedRequest);
        when(itemRequestMapper.toItemRequestDto(updatedRequest)).thenReturn(expectedDto);

        // When
        ItemRequestDto result = itemRequestService.update(requestId, updateDto);

        // Then
        assertNotNull(result);
        assertEquals("Updated description", result.getDescription());

        verify(itemRequestRepository).findById(requestId);
        verify(itemRequestRepository).save(existingRequest);
        verify(itemRequestMapper).toItemRequestDto(updatedRequest);
    }

    @Test
    void delete_ShouldDeleteItemRequest() {
        // Given
        Long requestId = 1L;

        // When
        itemRequestService.delete(requestId);

        // Then
        verify(itemRequestRepository).deleteById(requestId);
    }
}