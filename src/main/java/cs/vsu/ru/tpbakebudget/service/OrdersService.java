package cs.vsu.ru.tpbakebudget.service;

import cs.vsu.ru.tpbakebudget.enums.OrderStatus;
import cs.vsu.ru.tpbakebudget.model.Orders;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
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

    boolean existsByUserIdAndName(Long id, String name);

    List<Orders> findByUserIdAndCreationDateBetween(Long userId, LocalDate startCreatedAt, LocalDate endCreatedAt);

    List<Orders> findByUserIdAndFinishDateBetween(Long userId, LocalDate startFinishedAt, LocalDate endFinishedAt);
}