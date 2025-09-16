package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 * DTO для передачи данных о запросе вещи.
 */
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
    @NotBlank(message = "Описание не может быть пустым")
    private String description;

    /**
     * Пользователь, создавший запрос.
     */
    @NotNull(message = "ItemRequest requestor не должен быть null")
    @NotBlank(message = "ItemRequest requestor не должен быть пустым")
    private Long requestorId;

    /**
     * Дата и время создания запроса.
     * Устанавливается автоматически на сервере
     */
    @NotNull(message = "Дата начала ItemRequest не может быть null")
    @PastOrPresent(message = "Дата начала ItemRequest должна быть в прошлом или настоящем")
    private LocalDateTime created;

}