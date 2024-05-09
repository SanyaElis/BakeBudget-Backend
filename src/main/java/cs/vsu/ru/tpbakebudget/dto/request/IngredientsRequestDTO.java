package cs.vsu.ru.tpbakebudget.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class IngredientsRequestDTO {

    @Schema(description = "Ingredient Name", example = "milk")
    @NotBlank(message = "Ingredient name cannot be blank")
    @Size(min = 2, max = 50, message = "Ingredient name must be from 2 to 50")
    private String name;

    @Schema(description = "Ingredient weight", example = "50")
    @DecimalMin(value = "0", message = "Weight must be > 0")
    @DecimalMax(value = "100000", message = "Weight must be < 100000")
    private double weight;

    @Schema(description = "Ingredient cost", example = "50")
    @DecimalMin(value = "0", message = "Cost must be > 0")
    @DecimalMax(value = "1000000", message = "Cost must be < 1000000")
    private double cost;
}

