package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {
    private Long id;

    @NotBlank(message = "Описание не может быть пустым")
    private String description;

    @NotNull(message = "ItemRequest requestor не должен быть null")
    private Long requestorId;

    @NotNull(message = "Дата начала ItemRequest не может быть null")
    @PastOrPresent(message = "Дата начала ItemRequest должна быть в прошлом или настоящем")
    private LocalDateTime created;

    private List<ItemResponseDto> items;

    public ItemRequestDto(Long id, String description, Long requestorId, LocalDateTime created) {
        this.id = id;
        this.description = description;
        this.requestorId = requestorId;
        this.created = created;
        this.items = List.of();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemResponseDto {
        private Long id;
        private String name;
        private String description;
        private Boolean available;
        private Long requestId;
        private Long ownerId;
    }
}