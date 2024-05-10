package cs.vsu.ru.tpbakebudget.dto.request.products;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProductsRequestDTO {

    @Schema(description = "Product Name", example = "Napoleon")
    @NotBlank(message = "Product name cannot be blank")
    @Size(min = 2, max = 50, message = "Product name must be from 2 to 50")
    private String name;

    @Schema(description = "Product weight", example = "50")
    @DecimalMin(value = "0", message = "Weight must be between 0 and 100000")
    @DecimalMax(value = "100000", message = "Weight must be between 0 and 100000")
    private Double weight;
}
