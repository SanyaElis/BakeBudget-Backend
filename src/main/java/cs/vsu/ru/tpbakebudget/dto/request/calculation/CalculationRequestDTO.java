package cs.vsu.ru.tpbakebudget.dto.request.calculation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CalculationRequestDTO {
    @Schema(description = "Extra expenses for the calculation", example = "50.00")
    @DecimalMin(value = "0", message = "Extra expenses must be non-negative")
    @DecimalMax(value = "10000000", message = "Extra expenses must be less than or equal to 10000000")
    private double extraExpenses;

    @Schema(description = "Margin factor for the order", example = "1.2")
    @DecimalMin(value = "0", message = "Margin factor must be non-negative")
    @DecimalMax(value = "10000", message = "Margin factor must be less than or equal to 10000")
    private double marginFactor;

    @Schema(description = "Final weight for the calculation", example = "500")
    @NotNull(message = "Final weight cannot be null")
    @DecimalMin(value = "0", message = "Final weight must be non-negative")
    @DecimalMax(value = "10000000", message = "Final weight must be less than or equal to 10000000")
    private double finalWeight;

    @Schema(description = "ID of the product for the calculation", example = "1")
    @NotNull(message = "Product Id cannot be null")
    private Long productId;
}
