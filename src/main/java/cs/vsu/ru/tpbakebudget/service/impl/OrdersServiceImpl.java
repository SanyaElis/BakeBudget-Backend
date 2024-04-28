package cs.vsu.ru.tpbakebudget.service.impl;

import cs.vsu.ru.tpbakebudget.enums.OrderStatus;
import cs.vsu.ru.tpbakebudget.model.Orders;
import cs.vsu.ru.tpbakebudget.model.Users;
import cs.vsu.ru.tpbakebudget.repository.OrdersRepository;
import cs.vsu.ru.tpbakebudget.service.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class OrdersServiceImpl implements OrdersService {

    private final OrdersRepository repository;

    @Autowired
    public OrdersServiceImpl(OrdersRepository repository) {
        this.repository = repository;
    }

    @Override
    public Orders save(Orders order) {
        return repository.save(order);
    }

    @Override
    public Orders findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public List<Orders> saveAll(List<Orders> ordersList) {
        return repository.saveAll(ordersList);
    }

    @Override
    public List<Orders> findAll() {
        return repository.findAll();
    }

    @Override
    public List<Orders> findAllByUserId(Long id) {
        return repository.findAllByUserId(id);
    }

    @Override
    public Orders update(Long id, Orders newOrder) {
        Orders order = repository.findById(id).orElse(null);
        if (order == null) {
            return null;
        }
        newOrder.setId(id);
        return repository.save(newOrder);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<Orders> findByProductId(Long id) {
        return repository.findByProductId(id);
    }

    @Override
    public List<Orders> findByUserAndCreationDateBetweenAndFinishDateBetween(Users user, Date startCreatedAt, Date endCreatedAt, Date startFinishedAt, Date endFinishedAt) {
        return repository.findByUserAndCreationDateBetweenAndFinishDateBetween(user, startCreatedAt, endCreatedAt, startFinishedAt, endFinishedAt);
    }

    @Override
    public Orders updateOrderStatus(Long id, OrderStatus newStatus) {
        Orders order = repository.findById(id).orElse(null);
        if (order == null) {
            return null;
        }
        order.setStatus(newStatus);
        return repository.save(order);
    }
}
