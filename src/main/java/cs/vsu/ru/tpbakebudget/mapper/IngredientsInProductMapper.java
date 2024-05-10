package cs.vsu.ru.tpbakebudget.mapper;

import cs.vsu.ru.tpbakebudget.dto.responce.products.IngredientsInProductResponseDTO;
import cs.vsu.ru.tpbakebudget.model.Ingredients;
import cs.vsu.ru.tpbakebudget.model.IngredientsInProduct;
import cs.vsu.ru.tpbakebudget.model.IngredientsInProductKey;
import cs.vsu.ru.tpbakebudget.model.Products;
import org.springframework.stereotype.Component;

@Component
public class IngredientsInProductMapper {
    public IngredientsInProduct toEntity(Ingredients ingredient, Products product, double weight) {
        IngredientsInProductKey key = new IngredientsInProductKey(ingredient, product);
        return new IngredientsInProduct(key, weight);
    }

    public IngredientsInProductResponseDTO toDto(IngredientsInProduct entity) {
        IngredientsInProductResponseDTO dto = new IngredientsInProductResponseDTO();
        dto.setProductId(entity.getPk().getProduct().getId());
        dto.setIngredientId(entity.getPk().getIngredient().getId());
        dto.setWeight(entity.getWeight());
        return dto;
    }
}
