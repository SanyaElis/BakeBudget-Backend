package cs.vsu.ru.tpbakebudget.service;

import cs.vsu.ru.tpbakebudget.exception.NotFoundException;
import cs.vsu.ru.tpbakebudget.model.Outgoings;
import cs.vsu.ru.tpbakebudget.model.Products;
import cs.vsu.ru.tpbakebudget.repository.OutgoingsRepository;
import cs.vsu.ru.tpbakebudget.service.impl.OutgoingsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OutgoingsServiceImplTests {

    @Mock
    private OutgoingsRepository repository;

    @InjectMocks
    private OutgoingsServiceImpl service;

    private Outgoings outgoing1;
    private Outgoings outgoing2;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        Products product = new Products();
        product.setId(1L);

        outgoing1 = new Outgoings();
        outgoing1.setId(1L);
        outgoing1.setName("Rent");
        outgoing1.setProduct(product);

        outgoing2 = new Outgoings();
        outgoing2.setId(2L);
        outgoing2.setName("Electricity");
        outgoing2.setProduct(product);
    }

    @Test
    public void testSaveNewOutgoing() {
        when(repository.existsByProductIdAndName(1L, "Rent")).thenReturn(false);
        when(repository.save(outgoing1)).thenReturn(outgoing1);

        Outgoings savedOutgoing = service.save(outgoing1);

        assertNotNull(savedOutgoing);
        assertEquals(outgoing1.getName(), savedOutgoing.getName());
        verify(repository, times(1)).existsByProductIdAndName(1L, "Rent");
        verify(repository, times(1)).save(outgoing1);
    }

    @Test
    public void testSaveExistingOutgoing() {
        when(repository.existsByProductIdAndName(1L, "Rent")).thenReturn(true);

        Outgoings savedOutgoing = service.save(outgoing1);

        assertNull(savedOutgoing);
        verify(repository, times(1)).existsByProductIdAndName(1L, "Rent");
        verify(repository, times(0)).save(outgoing1);
    }

    @Test
    public void testUpdateOutgoing() {
        when(repository.findById(1L)).thenReturn(Optional.of(outgoing1));
        when(repository.existsByProductIdAndName(1L, "Electricity")).thenReturn(false);
        when(repository.save(outgoing2)).thenReturn(outgoing2);

        Outgoings updatedOutgoing = service.update(1L, outgoing2);

        assertNotNull(updatedOutgoing);
        assertEquals(1L, updatedOutgoing.getId());
        assertEquals("Electricity", updatedOutgoing.getName());
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).existsByProductIdAndName(1L, "Electricity");
        verify(repository, times(1)).save(outgoing2);
    }

    @Test
    public void testUpdateOutgoingWithDuplicateName() {
        when(repository.findById(1L)).thenReturn(Optional.of(outgoing1));
        when(repository.existsByProductIdAndName(1L, "Electricity")).thenReturn(true);

        Outgoings updatedOutgoing = service.update(1L, outgoing2);

        assertNull(updatedOutgoing);
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).existsByProductIdAndName(1L, "Electricity");
        verify(repository, times(0)).save(outgoing2);
    }

    @Test
    public void testFindAll() {
        when(repository.findAll()).thenReturn(Arrays.asList(outgoing1, outgoing2));

        List<Outgoings> outgoings = service.findAll();

        assertNotNull(outgoings);
        assertEquals(2, outgoings.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    public void testFindById() {
        when(repository.findById(1L)).thenReturn(Optional.of(outgoing1));

        Outgoings foundOutgoing = service.findById(1L);

        assertNotNull(foundOutgoing);
        assertEquals(1L, foundOutgoing.getId());
        verify(repository, times(1)).findById(1L);
    }

    @Test
    public void testFindByIdNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.findById(1L));
        verify(repository, times(1)).findById(1L);
    }

    @Test
    public void testSaveAll() {
        List<Outgoings> outgoingsList = Arrays.asList(outgoing1, outgoing2);
        when(repository.saveAll(outgoingsList)).thenReturn(outgoingsList);

        List<Outgoings> savedOutgoings = service.saveAll(outgoingsList);

        assertNotNull(savedOutgoings);
        assertEquals(2, savedOutgoings.size());
        verify(repository, times(1)).saveAll(outgoingsList);
    }

    @Test
    public void testDelete() {
        service.delete(1L);

        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    public void testFindAllByProductId() {
        when(repository.findAllByProductId(1L)).thenReturn(Arrays.asList(outgoing1, outgoing2));

        List<Outgoings> outgoings = service.findAllByProductId(1L);

        assertNotNull(outgoings);
        assertEquals(2, outgoings.size());
        verify(repository, times(1)).findAllByProductId(1L);
    }

    @Test
    public void testExistsByProductIdAndName() {
        when(repository.existsByProductIdAndName(1L, "Rent")).thenReturn(true);

        boolean exists = service.existsByProductIdAndName(1L, "Rent");

        assertTrue(exists);
        verify(repository, times(1)).existsByProductIdAndName(1L, "Rent");
    }
}
