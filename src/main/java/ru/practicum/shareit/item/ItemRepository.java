package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private Long nextId = 1L;

    public Item save(Item item) {
        if (item.getId() == null) {
            item.setId(nextId++);
        }
        items.put(item.getId(), item);
        return item;
    }

    public Optional<Item> findById(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    public List<Item> findAll() {
        return new ArrayList<>(items.values());
    }

    public List<Item> findByOwnerId(Long ownerId) {
        List<Item> result = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getOwnerId().equals(ownerId)) {
                result.add(item);
            }
        }
        return result;
    }

    public Item update(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    public void deleteById(Long id) {
        items.remove(id);
    }

    public List<Item> search(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        List<Item> result = new ArrayList<>();
        String searchText = text.toLowerCase();

        for (Item item : items.values()) {
            if (Boolean.TRUE.equals(item.getAvailable()) &&
                    (item.getName().toLowerCase().contains(searchText) ||
                            item.getDescription().toLowerCase().contains(searchText))) {
                result.add(item);
            }
        }
        return result;
    }

    public boolean existsById(Long id) {
        return items.containsKey(id);
    }

    public boolean isOwner(Long itemId, Long userId) {
        Item item = items.get(itemId);
        return item != null && item.getOwnerId().equals(userId);
    }
}