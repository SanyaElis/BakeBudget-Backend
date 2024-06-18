package cs.vsu.ru.tpbakebudget.service;

import cs.vsu.ru.tpbakebudget.model.IngredientsInProduct;
import cs.vsu.ru.tpbakebudget.model.IngredientsInProductKey;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface IngredientsInProductService {
    IngredientsInProduct save(IngredientsInProduct ingredientsInProduct);

    IngredientsInProduct findById(IngredientsInProductKey pk);

    List<IngredientsInProduct> findAll();

    IngredientsInProduct update(IngredientsInProduct ingredientsInProduct);

    void delete(IngredientsInProductKey pk);

    List<IngredientsInProduct> findByPk_ProductId(Long productId);

    boolean existsByPk(IngredientsInProductKey key);
}
