package cs.vsu.ru.tpbakebudget.dto.response.products;

import lombok.Data;

@Data
public class IngredientsInProductResponseDTO {
    private Long ingredientId;

    private Long productId;

    private double weight;
}
