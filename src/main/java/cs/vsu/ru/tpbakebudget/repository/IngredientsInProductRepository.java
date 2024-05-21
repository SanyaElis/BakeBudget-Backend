package cs.vsu.ru.tpbakebudget.repository;

import cs.vsu.ru.tpbakebudget.model.IngredientsInProduct;
import cs.vsu.ru.tpbakebudget.model.IngredientsInProductKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IngredientsInProductRepository extends JpaRepository<IngredientsInProduct, IngredientsInProductKey> {
    List<IngredientsInProduct> findByPk_ProductId(Long productId);

    boolean existsByPk(IngredientsInProductKey key);
}
