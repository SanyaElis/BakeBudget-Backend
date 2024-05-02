package cs.vsu.ru.tpbakebudget.service;

import cs.vsu.ru.tpbakebudget.enums.OrderStatus;
import cs.vsu.ru.tpbakebudget.model.Orders;

import java.time.LocalDate;
import java.util.List;

public interface OrdersService {
    Orders save(Orders order);

    Orders findById(Long id);

    List<Orders> saveAll(List<Orders> ordersList);

    List<Orders> findAllByUserId(Long id);

    Orders update(Long id, Orders order);

    void delete(Long id);

    List<Orders> findByProductId(Long id);

    List<Orders> findByUserIdAndCreationDateBetweenAndFinishDateBetween(Long userId, LocalDate startCreatedAt, LocalDate endCreatedAt, LocalDate startFinishedAt, LocalDate endFinishedAt);

    void updateOrderStatus(Long id, OrderStatus newStatus);
}
