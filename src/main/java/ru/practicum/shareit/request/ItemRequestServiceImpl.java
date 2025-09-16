package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserRepository;

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
    private final UserRepository userRepository;

    /**
     * Создает запрос, предварительно проверив существование пользователя
     */
    @Override
    public ItemRequestDto create(ItemRequestDto itemRequestDto, Long requestorId) {
        // Устанавливаем ID пользователя из параметра
        itemRequestDto.setRequestorId(requestorId);

        // Проверяем существование пользователя через маппер
        var itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, userRepository);

        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    /**
     * Находит запрос по ID
     */
    @Override
    public ItemRequestDto getById(Long id) {
        var itemRequest = itemRequestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item request not found with id: " + id));
        return ItemRequestMapper.toItemRequestDto(itemRequest);
    }

    /**
     * Возвращает запросы текущего пользователя
     */
    @Override
    public List<ItemRequestDto> getByRequestorId(Long requestorId) {
        return itemRequestRepository.findByRequestorId(requestorId).stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    /**
     * Возвращает запросы других пользователей
     */
    @Override
    public List<ItemRequestDto> getAllExceptRequestor(Long userId) {
        return itemRequestRepository.findAllExceptRequestor(userId).stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    /**
     * Обновляет запрос
     */
    @Override
    public ItemRequestDto update(Long id, ItemRequestDto itemRequestDto) {
        var existingRequest = itemRequestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item request not found with id: " + id));

        if (itemRequestDto.getDescription() != null) {
            existingRequest.setDescription(itemRequestDto.getDescription());
        }

        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.update(existingRequest));
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