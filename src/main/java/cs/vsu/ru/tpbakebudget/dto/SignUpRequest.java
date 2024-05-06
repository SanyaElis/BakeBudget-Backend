package cs.vsu.ru.tpbakebudget.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignUpRequest {

    @Schema(description = "Username", example = "Alexander")
    @Size(min = 3, max = 50, message = "Username must be from 3 to 50")
    @NotBlank(message = "Username must not be empty")
    private String username;

    @Schema(description = "Email address", example = "alex@gmail.com")
    @Size(min = 5, max = 255, message = "Email must be from 5 to 55")
    @NotBlank(message = "Email must not be empty")
    @Email(message = "Format - user@example.com")
    private String email;

    @Schema(description = "Password", example = "my_1secret1_password")
    @Size(min = 8, max = 255, message = "Password length must from 8 to 255")
    @NotBlank(message = "Password must not be empty")
    private String password;
}
