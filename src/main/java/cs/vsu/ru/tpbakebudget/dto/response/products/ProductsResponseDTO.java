package cs.vsu.ru.tpbakebudget.dto.response.products;

import lombok.Data;

@Data
public class ProductsResponseDTO {
    private Long id;
    private String name;
    private double weight;
}
