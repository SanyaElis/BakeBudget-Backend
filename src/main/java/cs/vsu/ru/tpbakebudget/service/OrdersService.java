package cs.vsu.ru.tpbakebudget.service;

import cs.vsu.ru.tpbakebudget.enums.OrderStatus;
import cs.vsu.ru.tpbakebudget.model.Orders;
import cs.vsu.ru.tpbakebudget.model.Users;

import java.util.Date;
import java.util.List;

public interface OrdersService {
    Orders save(Orders order);

    Orders findById(Long id);

    List<Orders> saveAll(List<Orders> ordersList);

    List<Orders> findAll();

    List<Orders> findAllByUserId(Long id);

    Orders update(Long id, Orders order);

    void delete(Long id);

    List<Orders> findByProductId(Long id);

    List<Orders> findByUserAndCreationDateBetweenAndFinishDateBetween(Users user, Date startCreatedAt, Date endCreatedAt, Date startFinishedAt, Date endFinishedAt);

    Orders updateOrderStatus(Long id, OrderStatus newStatus);
}
