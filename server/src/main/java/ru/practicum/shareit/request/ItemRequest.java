package ru.practicum.shareit.request;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemRequest {
    /**
     * Уникальный идентификатор запроса.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Описание запрашиваемой вещи.
     */
    @Column(name = "description", nullable = false, length = 1000)
    private String description;

    /**
     * Пользователь, создавший запрос.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;

    /**
     * Дата и время создания запроса.
     */
    @Column(name = "created", nullable = false)
    private LocalDateTime created;
}