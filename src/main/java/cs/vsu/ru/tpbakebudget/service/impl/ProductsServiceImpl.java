package cs.vsu.ru.tpbakebudget.service.impl;

import cs.vsu.ru.tpbakebudget.model.Products;
import cs.vsu.ru.tpbakebudget.repository.ProductsRepository;
import cs.vsu.ru.tpbakebudget.service.ProductsService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Component
public class ProductsServiceImpl implements ProductsService {
    private final ProductsRepository repository;

    @Autowired
    public ProductsServiceImpl(ProductsRepository repository) {
        this.repository = repository;
    }

    public List<Products> findAll() {
        return repository.findAll();
    }

    @Override
    public Products update(Long id, @NotNull Products newProduct) {
        Products product = repository.findById(id).orElseThrow();
        product.setName(newProduct.getName());
        product.setWeight(newProduct.getWeight());
        product.setMinioPictureName(newProduct.getMinioPictureName());
        return repository.save(product);
    }

    @Override
    public Products save(Products product) {
        return repository.save(product);
    }

    @Override
    public Products findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public List<Products> saveAll(List<Products> products) {
        return repository.saveAll(products);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    public void setMinioName(Long id, String minioName) {
        Products product = repository.findById(id).orElse(null);
        if (product != null) {
            product.setMinioPictureName(minioName);
            repository.save(product);
        } else {
            throw new IllegalArgumentException();
        }
    }
}
