package cs.vsu.ru.tpbakebudget.service;

import cs.vsu.ru.tpbakebudget.model.IngredientsInProduct;
import cs.vsu.ru.tpbakebudget.model.IngredientsInProductKey;

import java.util.List;

public interface IngredientsInProductService {
    IngredientsInProduct save(IngredientsInProduct ingredientsInProduct);

    IngredientsInProduct findById(IngredientsInProductKey pk);

    List<IngredientsInProduct> saveAll(List<IngredientsInProduct> ingredientsInProductList);

    List<IngredientsInProduct> findAll();

    IngredientsInProduct update(IngredientsInProductKey pk, IngredientsInProduct ingredientsInProduct);

    void delete(IngredientsInProductKey pk);
}
