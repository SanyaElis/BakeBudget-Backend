package cs.vsu.ru.tpbakebudget.service;

import cs.vsu.ru.tpbakebudget.model.Ingredients;

import java.util.List;

public interface IngredientsService {
    Ingredients save(Ingredients ingredient);

    Ingredients findById(Long id);

    List<Ingredients> saveAll(List<Ingredients> ingredientsList);

    List<Ingredients> findAllByUserId(Long id);

    Ingredients update(Long id, Ingredients ingredient);

    void delete(Long id);
}
