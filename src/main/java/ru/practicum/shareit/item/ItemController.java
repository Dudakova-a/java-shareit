package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import jakarta.validation.Valid;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto createItem(@Valid @RequestBody ItemDto itemDto,
                              @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("POST /items - Creating item: {}, ownerId: {}", itemDto, ownerId);
        ItemDto result = itemService.createItem(itemDto, ownerId);
        log.info("POST /items - Item created successfully: {}", result);
        return result;
    }

    @GetMapping("/{id}")
    public ItemDto getItemById(@PathVariable Long id) {
        log.info("GET /items/{} - Getting item by id", id);
        ItemDto result = itemService.getItemById(id);
        log.info("GET /items/{} - Item found: {}", id, result);
        return result;
    }

    @GetMapping
    public List<ItemDto> getItemsByOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("GET /items - Getting items for ownerId: {}", ownerId);
        List<ItemDto> result = itemService.getItemsByOwner(ownerId);
        log.info("GET /items - Found {} items for ownerId: {}", result.size(), ownerId);
        return result;
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(@PathVariable Long id,
                              @RequestBody ItemDto itemDto,
                              @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("PATCH /items/{} - Updating item: {}, ownerId: {}", id, itemDto, ownerId);
        ItemDto result = itemService.updateItem(id, itemDto, ownerId);
        log.info("PATCH /items/{} - Item updated successfully: {}", id, result);
        return result;
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        log.info("GET /items/search - Searching items with text: '{}'", text);
        List<ItemDto> result = itemService.searchItems(text);
        log.info("GET /items/search - Found {} items for text: '{}'", result.size(), text);
        return result;
    }
}