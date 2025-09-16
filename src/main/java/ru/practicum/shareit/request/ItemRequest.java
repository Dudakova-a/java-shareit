package ru.practicum.shareit.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 * Модель запроса вещи.
 * Пользователь создает запрос, если не находит нужную вещь в системе.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequest {
    /**
     * Уникальный идентификатор запроса.
     */
    @NotBlank(message = "ItemRequest Id не должен быть пустым")
    private Long id;

    /**
     * Текст запроса с описанием требуемой вещи.
     */
    @NotNull(message = "ItemRequest description не должен быть null")
    @NotBlank(message = "ItemRequest description не должен быть пустым")
    private String description;

    /**
     * Пользователь, создавший запрос.
     * Много запросов могут быть от одного пользователя(связь many-to-one).
     */
    @NotNull(message = "ItemRequest requestor не должен быть null")
    @NotBlank(message = "ItemRequest requestor не должен быть пустым")
    private Long requestorId;

    /**
     * Дата и время создания запроса.
     * Устанавливается автоматически при создании.
     */
    @NotNull(message = "Дата начала ItemRequest не может быть null")
    @PastOrPresent(message = "Дата начала ItemRequest должна быть в прошлом или настоящем")
    private LocalDateTime created;
}