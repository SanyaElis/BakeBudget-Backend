package cs.vsu.ru.tpbakebudget.service.impl;

import cs.vsu.ru.tpbakebudget.model.Ingredients;
import cs.vsu.ru.tpbakebudget.repository.IngredientsRepository;
import cs.vsu.ru.tpbakebudget.service.IngredientsService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Component
public class IngredientsServiceImpl implements IngredientsService {

    private final IngredientsRepository repository;

    @Autowired
    public IngredientsServiceImpl(IngredientsRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Ingredients> findAllByUserId(Long id) {
        return repository.findAllByUserId(id);
    }

    @Override
    public Ingredients save(Ingredients ingredients) {
        return repository.save(ingredients);
    }

    @Override
    public Ingredients update(Long id, @NotNull Ingredients newIngredient) {
        Ingredients ingredient = repository.findById(id).orElse(null);
        if (ingredient == null) {
            return null;
        } else {
            newIngredient.setId(id);
            return repository.save(newIngredient);
        }
    }

    @Override
    public Ingredients findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public List<Ingredients> saveAll(List<Ingredients> ingredients) {
        return repository.saveAll(ingredients);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

}
