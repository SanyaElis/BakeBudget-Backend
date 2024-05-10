package cs.vsu.ru.tpbakebudget.dto.responce.ingredients;

import lombok.Data;

@Data
public class IngredientsResponseDTO {
    private Long id;
    private String name;
    private double weight;
    private double cost;
}
