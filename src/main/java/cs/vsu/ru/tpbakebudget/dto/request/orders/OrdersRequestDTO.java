package cs.vsu.ru.tpbakebudget.dto.request.orders;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class OrdersRequestDTO {

    @Schema(description = "Name of the order", example = "Chocolate Cake")
    @NotBlank(message = "Name cannot be blank")
    @NotNull(message = "Name cannot be null")
    @Size(min = 2, max = 50, message = "Name must be from 2 to 50 characters")
    private String name;

    @Schema(description = "Description of the order", example = "Sweet chocolate cake")
    @Size(max = 100, message = "Description must be at most 100 characters")
    private String description;

    @Schema(description = "Extra expenses for the order", example = "50.00", minimum = "0", maximum = "10000000")
    @DecimalMin(value = "0", message = "Extra expenses must be non-negative")
    @DecimalMax(value = "10000000", message = "Extra expenses must be less than or equal to 10000000")
    private double extraExpenses;

    @Schema(description = "Final weight of the order", example = "2.5")
    @NotNull(message = "Final weight cannot be null")
    @DecimalMin(value = "0", message = "Final weight must be non-negative")
    @DecimalMax(value = "10000000", message = "Final weight must be less than or equal to 10000000")
    private double finalWeight;

    @Schema(description = "Margin factor for the order", example = "1.2")
    @DecimalMin(value = "0", message = "Margin factor must be non-negative")
    @DecimalMax(value = "10000", message = "Margin factor must be less than or equal to 10000")
    private double marginFactor;

    @Schema(description = "Date of order creation", example = "2024-04-29", pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate creationDate;

    @Schema(description = "Date of order finish", example = "2024-05-05", pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate finishDate;

    @Schema(description = "ID of the product with the order", example = "1")
    @NotNull(message = "Product Id cannot be null")
    private Long productId;
}
