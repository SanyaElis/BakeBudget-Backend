package cs.vsu.ru.tpbakebudget.service.impl;

import cs.vsu.ru.tpbakebudget.exception.NotFoundException;
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
        repository.findById(id).orElseThrow(() -> new NotFoundException("Product not found with id: " + id));
        newProduct.setId(id);
        return repository.save(newProduct);
    }

    @Override
    public Products save(Products product) {
        return repository.save(product);
    }

    @Override
    public Products findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Product not found with id: " + id));
    }

    @Override
    public List<Products> saveAll(List<Products> products) {
        return repository.saveAll(products);
    }

    @Override
    public List<Products> findAllByUserId(Long id) {
        return repository.findAllByUserId(id);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    public void setMinioName(Products product, String minioName) {
        product.setMinioPictureName(minioName);
        repository.save(product);
    }
}
