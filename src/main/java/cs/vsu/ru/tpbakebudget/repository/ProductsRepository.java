package cs.vsu.ru.tpbakebudget.repository;

import cs.vsu.ru.tpbakebudget.model.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductsRepository extends JpaRepository<Products, Long> {
    List<Products> findAllByUserId(Long id);

    boolean existsByUserIdAndName(Long id, String name);
}