package cs.vsu.ru.tpbakebudget.repository;


import cs.vsu.ru.tpbakebudget.model.Ingredients;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IngredientsRepository extends JpaRepository<Ingredients, Long> {
    List<Ingredients> findAllByUserId(Long id);

    List<Ingredients> findByIngredientsInProductPkProductId(Long productId);

    boolean existsByUserIdAndName(Long id, String name);
}
