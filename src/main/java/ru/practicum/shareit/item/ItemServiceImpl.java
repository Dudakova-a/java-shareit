package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;

import java.util.*;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;

    public ItemServiceImpl(ItemRepository itemRepository, UserService userService) {
        this.itemRepository = itemRepository;
        this.userService = userService;
    }

    @Override
    public ItemDto createItem(ItemDto itemDto, Long ownerId) {
        try {
            // Проверяем, что пользователь существует
            userService.getUserById(ownerId);
        } catch (NoSuchElementException e) {
            // Преобразуем в NotFoundException
            throw new NotFoundException("User not found with id: " + ownerId);
        }

        Item item = ItemMapper.toItem(itemDto, ownerId);
        Item savedItem = itemRepository.save(item);
        return ItemMapper.toItemDto(savedItem);
    }

    @Override
    public ItemDto getItemById(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Item not found with id: " + id));
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getItemsByOwner(Long ownerId) {
        // Проверяем, что пользователь существует
        userService.getUserById(ownerId);

        List<Item> items = itemRepository.findByOwnerId(ownerId);
        List<ItemDto> result = new ArrayList<>();
        for (Item item : items) {
            result.add(ItemMapper.toItemDto(item));
        }
        return result;
    }

    @Override
    public ItemDto updateItem(Long id, ItemDto itemDto, Long ownerId) {
        // Проверяем, что пользователь существует
        try {
            userService.getUserById(ownerId);
        } catch (NoSuchElementException e) {
            throw new NotFoundException("User not found with id: " + ownerId);
        }

        Item existingItem = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item not found with id: " + id));

        // ВАЖНО: Заменяем SecurityException на AccessDeniedException
        if (!existingItem.getOwnerId().equals(ownerId)) {
            throw new AccessDeniedException("Only owner can update item");
        }

        if (itemDto.getName() != null) {
            existingItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            existingItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            existingItem.setAvailable(itemDto.getAvailable());
        }

        Item updatedItem = itemRepository.update(existingItem);
        return ItemMapper.toItemDto(updatedItem);
    }


    @Override
    public List<ItemDto> searchItems(String text) {
        List<Item> items = itemRepository.search(text);
        List<ItemDto> result = new ArrayList<>();
        for (Item item : items) {
            result.add(ItemMapper.toItemDto(item));
        }
        return result;
    }
}