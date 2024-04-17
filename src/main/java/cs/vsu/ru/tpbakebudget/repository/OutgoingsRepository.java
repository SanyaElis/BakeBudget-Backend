package cs.vsu.ru.tpbakebudget.repository;


import cs.vsu.ru.tpbakebudget.model.Outgoings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OutgoingsRepository extends JpaRepository<Outgoings, Long> {
}
