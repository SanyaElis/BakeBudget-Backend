package cs.vsu.ru.tpbakebudget.repository;


import cs.vsu.ru.tpbakebudget.model.IngredientsInProduct;
import cs.vsu.ru.tpbakebudget.model.IngredientsInProductKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredientsInProductRepository extends JpaRepository<IngredientsInProduct, IngredientsInProductKey> {
}
