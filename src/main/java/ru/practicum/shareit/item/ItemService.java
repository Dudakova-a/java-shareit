package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, Long ownerId);

    ItemDto getItemById(Long id);

    List<ItemDto> getItemsByOwner(Long ownerId);

    ItemDto updateItem(Long id, ItemDto itemDto, Long ownerId);

    List<ItemDto> searchItems(String text);
}