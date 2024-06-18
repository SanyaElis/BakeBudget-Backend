package cs.vsu.ru.tpbakebudget.service;

import cs.vsu.ru.tpbakebudget.model.Ingredients;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface IngredientsService {
    Ingredients save(Ingredients ingredient);

    Ingredients findById(Long id);

    List<Ingredients> saveAll(List<Ingredients> ingredientsList);

    List<Ingredients> findAllByUserId(Long id);

    Ingredients update(Long id, Ingredients ingredient);

    void delete(Long id);

    List<Ingredients> findByIngredientsInProductPkProductId(Long productId);

    boolean existsByUserIdAndName(Long id, String name);
}
