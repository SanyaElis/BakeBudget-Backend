package cs.vsu.ru.tpbakebudget.service.impl;

import cs.vsu.ru.tpbakebudget.model.IngredientsInProduct;
import cs.vsu.ru.tpbakebudget.model.IngredientsInProductKey;
import cs.vsu.ru.tpbakebudget.repository.IngredientsInProductRepository;
import cs.vsu.ru.tpbakebudget.service.IngredientsInProductService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Component
public class IngredientsInProductServiceImpl implements IngredientsInProductService {
    private final IngredientsInProductRepository repository;

    @Autowired
    public IngredientsInProductServiceImpl(IngredientsInProductRepository repository) {
        this.repository = repository;
    }

    @Override
    public IngredientsInProduct save(IngredientsInProduct ingredientsInProduct) {
        return repository.save(ingredientsInProduct);
    }

    @Override
    public IngredientsInProduct findById(IngredientsInProductKey pk) {
        return repository.findById(pk).orElse(null);
    }

    @Override
    public List<IngredientsInProduct> saveAll(List<IngredientsInProduct> ingredientsInProductList) {
        return repository.saveAll(ingredientsInProductList);
    }

    @Override
    public List<IngredientsInProduct> findAll() {
        return repository.findAll();
    }

    @Override
    public IngredientsInProduct update(@NotNull IngredientsInProduct newIngredientsInProduct) {
        IngredientsInProduct ingredientsInProduct = repository.findById(newIngredientsInProduct.getPk()).orElse(null);
        if (ingredientsInProduct == null) {
            return null;
        }
        return repository.save(newIngredientsInProduct);
    }

    @Override
    public void delete(IngredientsInProductKey pk) {
        repository.deleteById(pk);
    }

    @Override
    public List<IngredientsInProduct> findByPk_ProductId(Long productId) {
        return repository.findByPk_ProductId(productId);
    }
}
