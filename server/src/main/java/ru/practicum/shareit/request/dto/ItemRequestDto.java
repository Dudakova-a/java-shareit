package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {
    /**
     * Уникальный идентификатор запроса.
     */
    private Long id;

    /**
     * Текст запроса с описанием требуемой вещи/
     * Не может быть пустым/
     */
    private String description;

    /**
     * Пользователь, создавший запрос.
     */
    private Long requestorId;

    /**
     * Дата и время создания запроса.
     * Устанавливается автоматически на сервере
     */
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
