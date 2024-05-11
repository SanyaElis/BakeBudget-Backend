package cs.vsu.ru.tpbakebudget.dto.request.outgoings;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class OutgoingsRequestDTO {

    @Schema(description = "Outgoing name", example = "electricity")
    @NotBlank(message = "Outgoing name cannot be blank")
    @Size(min = 2, max = 50, message = "Outgoing name must be from 2 to 50")
    private String name;

    @Schema(description = "Outgoing value", example = "50")
    @DecimalMin(value = "0", message = "Outgoing value must be between 0 and 1000000")
    @DecimalMax(value = "1000000", message = "Outgoing value must be between 0 and 1000000")
    private Double cost;

    @NotNull(message = "Product Id cannot be null")
    private Long productId;
}
