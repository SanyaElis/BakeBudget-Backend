package cs.vsu.ru.tpbakebudget.repository;

import cs.vsu.ru.tpbakebudget.model.Orders;
import cs.vsu.ru.tpbakebudget.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, Long> {
    List<Orders> findByProductId(Long productId);

    List<Orders> findAllByUserId(Long id);

    List<Orders> findByUserAndCreationDateBetweenAndFinishDateBetween(Users user, Date startCreatedAt, Date endCreatedAt, Date startFinishedAt, Date endFinishedAt);
}
