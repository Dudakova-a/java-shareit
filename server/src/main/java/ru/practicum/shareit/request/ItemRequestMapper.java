package ru.practicum.shareit.request;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Component
public class ItemRequestMapper {

    public ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getRequester().getId(),
                itemRequest.getCreated()
        );
    }

    public ItemRequest toItemRequest(ItemRequestCreateDto itemRequestCreateDto, User requester) {
        return ItemRequest.builder()
                .description(itemRequestCreateDto.getDescription())
                .requester(requester)
                .created(LocalDateTime.now())
                .build();
    }
}