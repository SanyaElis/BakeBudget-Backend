package cs.vsu.ru.tpbakebudget.dto.request.products;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class IngredientsInProductRequestDTO {

    @NotNull(message = "Ingredient Id cannot be null")
    private Long ingredientId;

    @NotNull(message = "Product Id cannot be null")
    private Long productId;

    @Schema(description = "Ingredient in product weight", example = "50")
    @DecimalMin(value = "0", message = "Weight must be > 0")
    @DecimalMax(value = "100000", message = "Weight must be < 100000")
    private double weight;
}
