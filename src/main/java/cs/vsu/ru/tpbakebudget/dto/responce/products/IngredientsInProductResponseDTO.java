package cs.vsu.ru.tpbakebudget.dto.responce.products;

import lombok.Data;

@Data
public class IngredientsInProductResponseDTO {
    private Long ingredientId;

    private Long productId;

    private double weight;
}
