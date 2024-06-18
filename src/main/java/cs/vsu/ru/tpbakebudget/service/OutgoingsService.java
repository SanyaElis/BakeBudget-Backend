package cs.vsu.ru.tpbakebudget.service;

import cs.vsu.ru.tpbakebudget.model.Outgoings;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface OutgoingsService {
    Outgoings save(Outgoings outgoing);

    Outgoings findById(Long id);

    List<Outgoings> saveAll(List<Outgoings> outgoingsList);

    List<Outgoings> findAll();

    Outgoings update(Long id, Outgoings outgoing);

    void delete(Long id);

    List<Outgoings> findAllByProductId(Long id);

    boolean existsByProductIdAndName(Long id, String name);
}
