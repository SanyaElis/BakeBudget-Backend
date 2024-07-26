package cs.vsu.ru.tpbakebudget.service.impl;

import cs.vsu.ru.tpbakebudget.exception.NotFoundException;
import cs.vsu.ru.tpbakebudget.model.Orders;
import cs.vsu.ru.tpbakebudget.model.Products;
import cs.vsu.ru.tpbakebudget.repository.OrdersRepository;
import cs.vsu.ru.tpbakebudget.repository.ProductsRepository;
import cs.vsu.ru.tpbakebudget.service.ProductsService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Component
public class ProductsServiceImpl implements ProductsService {
    private final ProductsRepository repository;

    private final OrdersRepository ordersRepository;

    @Autowired
    public ProductsServiceImpl(ProductsRepository repository, OrdersRepository ordersRepository) {
        this.repository = repository;
        this.ordersRepository = ordersRepository;
    }

    public List<Products> findAll() {
        return repository.findAll();
    }

    @Override
    public Products save(Products product) {
        if (!existsByUserIdAndName(product.getUser().getId(), product.getName())) {
            return repository.save(product);
        } else {
            return null;
        }
    }

    @Override
    public Products update(Long id, @NotNull Products newProduct) {
        Products product = repository.findById(id).orElseThrow(() -> new NotFoundException("Product not found with id: " + id));
        if(!Objects.equals(newProduct.getName(), product.getName()) && existsByUserIdAndName(newProduct.getUser().getId(), newProduct.getName())){
            return null;
        }
        product.setName(newProduct.getName());
        product.setWeight(newProduct.getWeight());
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

    @Transactional
    @Override
    public void delete(Long productId) {
        Products product = repository.findById(productId).orElseThrow(() -> new NotFoundException("Product not found with id: " + productId));

        List<Orders> orders = ordersRepository.findByProductId(productId);
        for (Orders order : orders) {
            order.setProduct(null);
            order.setMinioPictureName(product.getMinioPictureName());
            ordersRepository.save(order);
        }

        repository.delete(product);
    }

    @Override
    public boolean existsByUserIdAndName(Long id, String name) {
        return repository.existsByUserIdAndName(id, name);
    }

    public void setMinioName(Products product, String minioName) {
        product.setMinioPictureName(minioName);
        repository.save(product);
    }
}
