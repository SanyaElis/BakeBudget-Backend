package cs.vsu.ru.tpbakebudget.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequestDTO {

    @Schema(description = "Email address", example = "alex@gmail.com")
    @Size(min = 5, max = 255, message = "Email must be from 5 to 55")
    @NotBlank(message = "Email must not be empty")
    @Email(message = "Format - user@example.com")
    private String email;

    @Schema(description = "Password", example = "mypswd1111")
    @Size(min = 8, max = 255, message = "Password length must from 8 to 255")
    @NotBlank(message = "Password must not be empty")
    private String newPassword;
}
