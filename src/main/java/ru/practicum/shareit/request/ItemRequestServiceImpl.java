package ru.practicum.shareit.request;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.User;


import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация сервиса запросов вещей.
 */
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    // Внедрение зависимости репозитория через конструктор
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final UserRepository userRepository;

    /**
     * Создает запрос, предварительно проверив существование пользователя
     */
    @Override
    @Transactional
    public ItemRequestDto create(ItemRequestDto itemRequestDto, Long userId) {
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        ItemRequest itemRequest = itemRequestMapper.toItemRequest(itemRequestDto, requester);
        ItemRequest savedRequest = itemRequestRepository.save(itemRequest);

        return itemRequestMapper.toItemRequestDto(savedRequest);
    }


    /**
     * Находит запрос по ID
     */
    @Override
    @Transactional(readOnly = true)
    public ItemRequestDto getById(Long id) {
        ItemRequest itemRequest = itemRequestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item request not found with id: " + id));
        return itemRequestMapper.toItemRequestDto(itemRequest);
    }

    /**
     * Возвращает запросы текущего пользователя
     */
    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found with id: " + userId);
        }
        return itemRequestRepository.findByRequesterIdOrderByCreatedDesc(userId).stream()
                .map(itemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    /**
     * Возвращает запросы других пользователей
     */
    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getAllExceptUser(Long userId, int from, int size) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found with id: " + userId);
        }
        return itemRequestRepository.findByRequesterIdNotOrderByCreatedDesc(userId).stream()
                .skip(from)
                .limit(size)
                .map(itemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    /**
     * Обновляет запрос
     */
    @Override
    public ItemRequestDto update(Long id, ItemRequestDto itemRequestDto) {
        ItemRequest existingRequest = itemRequestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item request not found with id: " + id));

        if (itemRequestDto.getDescription() != null) {
            existingRequest.setDescription(itemRequestDto.getDescription());
        }

        return itemRequestMapper.toItemRequestDto(itemRequestRepository.save(existingRequest));
    }

    /**
     * Удаляет запрос.
     *
     * @param id идентификатор запроса для удаления.
     */
    @Override
    public void delete(Long id) {
        itemRequestRepository.deleteById(id);
    }
}