package com.aufsell.Lab1.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Запрос регистрации")
public class SignUpRequest {

    @Schema(description = "Имя пользователя", example = "Mark")
    @Size(min = 5, max = 30, message = "Имя должно содержать от 5 до 30 символов")
    @NotBlank(message = "Не может быть пустым")
    private String username;

    @Schema(description = "Пароль", example = "my_password")
    @Size(max=255, message = "Длина пароля не должна превышать 255 символов")
    private String password;
}
