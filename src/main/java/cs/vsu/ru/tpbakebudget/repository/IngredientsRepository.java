package cs.vsu.ru.tpbakebudget.repository;


import cs.vsu.ru.tpbakebudget.model.Ingredients;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredientsRepository extends JpaRepository<Ingredients, Long> {
}
