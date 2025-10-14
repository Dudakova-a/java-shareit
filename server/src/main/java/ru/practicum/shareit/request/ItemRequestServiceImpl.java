package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.User;


import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация сервиса запросов вещей
 */
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    // Внедрение зависимости репозитория через конструктор
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    /**
     * Создает запрос, предварительно проверив существование пользователя
     */
    @Override
    @Transactional
    public ItemRequestDto create(@Valid ItemRequestCreateDto itemRequestDto, Long userId) {
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        ItemRequest itemRequest = itemRequestMapper.toItemRequest(itemRequestDto, requester);
        ItemRequest savedRequest = itemRequestRepository.save(itemRequest);
        ItemRequestDto resultDto = itemRequestMapper.toItemRequestDto(savedRequest);
        resultDto.setItems(List.of()); // устанавливаем пустой список

        return resultDto;
    }


    /**
     * Находит запрос по ID
     */
    @Override
    @Transactional(readOnly = true)
    public ItemRequestDto getById(Long id) {
        ItemRequest itemRequest = itemRequestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item request not found with id: " + id));
        ItemRequestDto resultDto = itemRequestMapper.toItemRequestDto(itemRequest);

        List<Item> responseItems = itemRepository.findByRequestId(id);
        List<ItemRequestDto.ItemResponseDto> itemDtos = responseItems.stream()
                .map(item -> new ItemRequestDto.ItemResponseDto(
                        item.getId(),
                        item.getName(),
                        item.getDescription(),
                        item.getAvailable(),
                        item.getRequestId(),
                        item.getOwner().getId()
                ))
                .collect(Collectors.toList());

        resultDto.setItems(itemDtos);
        return resultDto;
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
                .map(request -> {
                    ItemRequestDto dto = itemRequestMapper.toItemRequestDto(request);

                    List<Item> responseItems = itemRepository.findByRequestId(request.getId());
                    List<ItemRequestDto.ItemResponseDto> itemDtos = responseItems.stream()
                            .map(item -> new ItemRequestDto.ItemResponseDto(
                                    item.getId(),
                                    item.getName(),
                                    item.getDescription(),
                                    item.getAvailable(),
                                    item.getRequestId(),
                                    item.getOwner().getId()
                            ))
                            .collect(Collectors.toList());

                    dto.setItems(itemDtos);
                    return dto;
                })
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
                .map(request -> {
                    ItemRequestDto dto = itemRequestMapper.toItemRequestDto(request);

                    // ↓↓↓ ДОБАВЛЯЕМ ВЕЩИ-ОТВЕТЫ ДЛЯ КАЖДОГО ЗАПРОСА ↓↓↓
                    List<Item> responseItems = itemRepository.findByRequestId(request.getId());
                    List<ItemRequestDto.ItemResponseDto> itemDtos = responseItems.stream()
                            .map(item -> new ItemRequestDto.ItemResponseDto(
                                    item.getId(),
                                    item.getName(),
                                    item.getDescription(),
                                    item.getAvailable(),
                                    item.getRequestId(),
                                    item.getOwner().getId()
                            ))
                            .collect(Collectors.toList());

                    dto.setItems(itemDtos);
                    return dto;
                })
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