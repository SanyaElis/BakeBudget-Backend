package cs.vsu.ru.tpbakebudget.service;

import cs.vsu.ru.tpbakebudget.model.Products;

import java.util.List;

public interface ProductsService {
    Products save(Products product);

    Products findById(Long id);

    List<Products> saveAll(List<Products> productsList);

    List<Products> findAll();

    Products update(Long id, Products product);

    void delete(Long id);
}
