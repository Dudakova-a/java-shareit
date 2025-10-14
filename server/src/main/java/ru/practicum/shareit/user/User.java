package ru.practicum.shareit.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    @NotNull(message = "User name не должен быть null")
    private String name;

    @Column(name = "email", nullable = false, unique = true, length = 512)
    @NotBlank(message = "Email не должен быть пустым")
    @Email(message = "Email должен соответствовать формату email")
    private String email;
}