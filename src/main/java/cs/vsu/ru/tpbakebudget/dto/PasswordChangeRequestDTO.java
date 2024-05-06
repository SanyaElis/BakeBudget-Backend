package cs.vsu.ru.tpbakebudget.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PasswordChangeRequestDTO {
    @Schema(description = "Old password", example = "old password")
    @Size(min = 8, max = 255, message = "Password length must from 8 to 255")
    @NotBlank(message = "Password must not be empty")
    private String oldPassword;

    @Schema(description = "New password", example = "new password")
    @Size(min = 8, max = 255, message = "Password length must from 8 to 255")
    @NotBlank(message = "Password must not be empty")
    private String newPassword;
}
