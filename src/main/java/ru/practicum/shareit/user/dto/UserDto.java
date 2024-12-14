package ru.practicum.shareit.user.dto;


import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    @Nullable
    private String name;

    @Nullable
    @Email(message = "Некорректный формат email")
    private String email;
}
