package cs.vsu.ru.tpbakebudget.service;

import cs.vsu.ru.tpbakebudget.model.Products;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ProductsService {
    Products save(Products product);

    Products findById(Long id);

    List<Products> saveAll(List<Products> productsList);

    List<Products> findAllByUserId(Long id);

    List<Products> findAll();

    Products update(Long id, Products product);

    void delete(Long id);

    boolean existsByUserIdAndName(Long id, String name);
}
