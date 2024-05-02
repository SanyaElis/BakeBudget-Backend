package cs.vsu.ru.tpbakebudget.service.impl;

import cs.vsu.ru.tpbakebudget.exception.NotFoundException;
import cs.vsu.ru.tpbakebudget.model.Outgoings;
import cs.vsu.ru.tpbakebudget.repository.OutgoingsRepository;
import cs.vsu.ru.tpbakebudget.service.OutgoingsService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Component
public class OutgoingsServiceImpl implements OutgoingsService {

    private final OutgoingsRepository repository;

    @Autowired
    public OutgoingsServiceImpl(OutgoingsRepository repository) {
        this.repository = repository;
    }

    @Override
    public Outgoings save(Outgoings outgoing) {
        return repository.save(outgoing);
    }

    @Override
    public Outgoings update(Long id, @NotNull Outgoings newOutgoing) {
        repository.findById(id).orElseThrow(() -> new NotFoundException("Outgoing not found with id: " + id));
        newOutgoing.setId(id);
        return repository.save(newOutgoing);
    }

    @Override
    public List<Outgoings> findAll() {
        return repository.findAll();
    }

    @Override
    public Outgoings findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Outgoing not found with id: " + id));
    }

    @Override
    public List<Outgoings> saveAll(List<Outgoings> outgoings) {
        return repository.saveAll(outgoings);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<Outgoings> findAllByProductId(Long id) {
        return repository.findAllByProductId(id);
    }
}
