package cs.vsu.ru.tpbakebudget.service;

import cs.vsu.ru.tpbakebudget.enums.OrderStatus;
import cs.vsu.ru.tpbakebudget.exception.NotFoundException;
import cs.vsu.ru.tpbakebudget.model.Orders;
import cs.vsu.ru.tpbakebudget.model.Users;
import cs.vsu.ru.tpbakebudget.repository.OrdersRepository;
import cs.vsu.ru.tpbakebudget.service.impl.OrdersServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrdersServiceImplTests {

    @Mock
    private OrdersRepository repository;

    @InjectMocks
    private OrdersServiceImpl service;

    private Orders order1;
    private Orders order2;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        Users user = new Users();
        user.setId(1L);

        order1 = new Orders();
        order1.setId(1L);
        order1.setName("Order1");
        order1.setUser(user);
        order1.setStatus(OrderStatus.NOT_STARTED);
        order1.setCreationDate(LocalDate.now().minusDays(5));

        order2 = new Orders();
        order2.setId(2L);
        order2.setName("Order2");
        order2.setUser(user);
        order2.setStatus(OrderStatus.IN_PROCESS);
        order2.setCreationDate(LocalDate.now().minusDays(3));
    }

    @Test
    public void testSaveNewOrder() {
        when(repository.existsByUserIdAndName(1L, "Order1")).thenReturn(false);
        when(repository.save(order1)).thenReturn(order1);

        Orders savedOrder = service.save(order1);

        assertNotNull(savedOrder);
        assertEquals(order1.getName(), savedOrder.getName());
        verify(repository, times(1)).existsByUserIdAndName(1L, "Order1");
        verify(repository, times(1)).save(order1);
    }

    @Test
    public void testSaveExistingOrder() {
        when(repository.existsByUserIdAndName(1L, "Order1")).thenReturn(true);

        Orders savedOrder = service.save(order1);

        assertNull(savedOrder);
        verify(repository, times(1)).existsByUserIdAndName(1L, "Order1");
        verify(repository, times(0)).save(order1);
    }

    @Test
    public void testFindById() {
        when(repository.findById(1L)).thenReturn(Optional.of(order1));

        Orders foundOrder = service.findById(1L);

        assertNotNull(foundOrder);
        assertEquals(1L, foundOrder.getId());
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
        List<Orders> ordersList = Arrays.asList(order1, order2);
        when(repository.saveAll(ordersList)).thenReturn(ordersList);

        List<Orders> savedOrders = service.saveAll(ordersList);

        assertNotNull(savedOrders);
        assertEquals(2, savedOrders.size());
        verify(repository, times(1)).saveAll(ordersList);
    }

    @Test
    public void testFindAllByUserId() {
        when(repository.findAllByUserId(1L)).thenReturn(Arrays.asList(order1, order2));

        List<Orders> orders = service.findAllByUserId(1L);

        assertNotNull(orders);
        assertEquals(2, orders.size());
        verify(repository, times(1)).findAllByUserId(1L);
    }

    @Test
    public void testUpdateOrder() {
        when(repository.findById(1L)).thenReturn(Optional.of(order1));
        when(repository.existsByUserIdAndName(1L, "Order2")).thenReturn(false);
        when(repository.save(order2)).thenReturn(order2);

        Orders updatedOrder = service.update(1L, order2);

        assertNotNull(updatedOrder);
        assertEquals(1L, updatedOrder.getId());
        assertEquals("Order2", updatedOrder.getName());
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).existsByUserIdAndName(1L, "Order2");
        verify(repository, times(1)).save(order2);
    }

    @Test
    public void testUpdateOrderWithDuplicateName() {
        when(repository.findById(1L)).thenReturn(Optional.of(order1));
        when(repository.existsByUserIdAndName(1L, "Order2")).thenReturn(true);

        Orders updatedOrder = service.update(1L, order2);

        assertNull(updatedOrder);
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).existsByUserIdAndName(1L, "Order2");
        verify(repository, times(0)).save(order2);
    }

    @Test
    public void testDelete() {
        service.delete(1L);

        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    public void testFindByProductId() {
        when(repository.findByProductId(1L)).thenReturn(Arrays.asList(order1, order2));

        List<Orders> orders = service.findByProductId(1L);

        assertNotNull(orders);
        assertEquals(2, orders.size());
        verify(repository, times(1)).findByProductId(1L);
    }

    @Test
    public void testFindByUserIdAndCreationDateBetweenAndFinishDateBetween() {
        LocalDate startCreatedAt = LocalDate.now().minusDays(10);
        LocalDate endCreatedAt = LocalDate.now();
        LocalDate startFinishedAt = LocalDate.now().minusDays(10);
        LocalDate endFinishedAt = LocalDate.now();

        when(repository.findByUserIdAndCreationDateBetweenAndFinishDateBetween(1L, startCreatedAt, endCreatedAt, startFinishedAt, endFinishedAt))
                .thenReturn(Arrays.asList(order1, order2));

        List<Orders> orders = service.findByUserIdAndCreationDateBetweenAndFinishDateBetween(1L, startCreatedAt, endCreatedAt, startFinishedAt, endFinishedAt);

        assertNotNull(orders);
        assertEquals(2, orders.size());
        verify(repository, times(1))
                .findByUserIdAndCreationDateBetweenAndFinishDateBetween(1L, startCreatedAt, endCreatedAt, startFinishedAt, endFinishedAt);
    }

    @Test
    public void testUpdateOrderStatus() {
        when(repository.findById(1L)).thenReturn(Optional.of(order1));

        service.updateOrderStatus(1L, OrderStatus.DONE);

        assertEquals(OrderStatus.DONE, order1.getStatus());
        assertNotNull(order1.getFinishDate());
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(order1);
    }

    @Test
    public void testExistsByUserIdAndName() {
        when(repository.existsByUserIdAndName(1L, "Order1")).thenReturn(true);

        boolean exists = service.existsByUserIdAndName(1L, "Order1");

        assertTrue(exists);
        verify(repository, times(1)).existsByUserIdAndName(1L, "Order1");
    }

    @Test
    public void testFindByUserIdAndCreationDateBetween() {
        LocalDate startCreatedAt = LocalDate.now().minusDays(10);
        LocalDate endCreatedAt = LocalDate.now();

        when(repository.findByUserIdAndCreationDateBetween(1L, startCreatedAt, endCreatedAt)).thenReturn(Arrays.asList(order1, order2));

        List<Orders> orders = service.findByUserIdAndCreationDateBetween(1L, startCreatedAt, endCreatedAt);

        assertNotNull(orders);
        assertEquals(2, orders.size());
        verify(repository, times(1)).findByUserIdAndCreationDateBetween(1L, startCreatedAt, endCreatedAt);
    }

    @Test
    public void testFindByUserIdAndFinishDateBetween() {
        LocalDate startFinishedAt = LocalDate.now().minusDays(10);
        LocalDate endFinishedAt = LocalDate.now();

        when(repository.findByUserIdAndFinishDateBetween(1L, startFinishedAt, endFinishedAt)).thenReturn(Arrays.asList(order1, order2));

        List<Orders> orders = service.findByUserIdAndFinishDateBetween(1L, startFinishedAt, endFinishedAt);

        assertNotNull(orders);
        assertEquals(2, orders.size());
        verify(repository, times(1)).findByUserIdAndFinishDateBetween(1L, startFinishedAt, endFinishedAt);
    }
}

