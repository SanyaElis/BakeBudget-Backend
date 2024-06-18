package cs.vsu.ru.tpbakebudget.controller;

import cs.vsu.ru.tpbakebudget.component.CostCounter;
import cs.vsu.ru.tpbakebudget.dto.request.calculation.CalculationRequestDTO;
import cs.vsu.ru.tpbakebudget.dto.request.orders.OrdersRequestDTO;
import cs.vsu.ru.tpbakebudget.dto.response.calculation.CalculationResponseDTO;
import cs.vsu.ru.tpbakebudget.dto.response.orders.OrdersResponseDTO;
import cs.vsu.ru.tpbakebudget.enums.OrderStatus;
import cs.vsu.ru.tpbakebudget.mapper.CalculationMapper;
import cs.vsu.ru.tpbakebudget.mapper.OrdersMapper;
import cs.vsu.ru.tpbakebudget.model.Orders;
import cs.vsu.ru.tpbakebudget.model.Products;
import cs.vsu.ru.tpbakebudget.model.Users;
import cs.vsu.ru.tpbakebudget.service.impl.OrdersServiceImpl;
import cs.vsu.ru.tpbakebudget.service.impl.ProductsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class OrdersControllerTest {

    @Mock
    private OrdersServiceImpl ordersService;

    @Mock
    private ProductsServiceImpl productsService;

    @Mock
    private CalculationMapper calculationMapper;

    @Mock
    private OrdersMapper ordersMapper;

    @Mock
    private CostCounter costCounter;

    private OrdersController ordersController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ordersController = new OrdersController(ordersService, productsService, calculationMapper, ordersMapper, costCounter);

        Users mockUser = new Users();
        mockUser.setId(1L);

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(mockUser);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testCalculate() {
        CalculationRequestDTO requestDTO = new CalculationRequestDTO();
        requestDTO.setProductId(1L);
        requestDTO.setFinalWeight(500);
        requestDTO.setExtraExpenses(100);
        requestDTO.setMarginFactor(1.5);

        Products product = new Products();
        when(productsService.findById(1L)).thenReturn(product);
        when(costCounter.countCost(product, 500, 100)).thenReturn(200.0);
        when(costCounter.countFinalCost(200.0, 1.5, 100)).thenReturn(400.0);

        CalculationResponseDTO responseDTO = new CalculationResponseDTO();
        responseDTO.setCostPrice(200.0);
        responseDTO.setFinalCost(400.0);
        when(calculationMapper.toDto(200.0, 400.0)).thenReturn(responseDTO);

        ResponseEntity<CalculationResponseDTO> response = ordersController.calculate(requestDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
    }

    @Test
    void testCreateOrder() {
        OrdersRequestDTO requestDTO = new OrdersRequestDTO();
        requestDTO.setProductId(1L);
        requestDTO.setFinalWeight(500);
        requestDTO.setExtraExpenses(100);
        requestDTO.setMarginFactor(1.5);

        Products product = new Products();
        Users user = new Users();
        user.setId(1L);

        Orders order = new Orders();
        order.setId(1L);
        order.setCostPrice(200);
        order.setFinalCost(400);
        order.setUser(user);

        when(productsService.findById(1L)).thenReturn(product);
        when(costCounter.countCost(product, 500, 100)).thenReturn(200.0);
        when(costCounter.countFinalCost(200.0, 1.5, 100)).thenReturn(400.0);
        when(ordersMapper.toEntity(requestDTO, product)).thenReturn(order);
        when(ordersService.save(any())).thenReturn(order);

        OrdersResponseDTO responseDTO = new OrdersResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setName("orderName");
        responseDTO.setDescription("orderDescription");
        responseDTO.setStatus(OrderStatus.NOT_STARTED);
        responseDTO.setCostPrice(200.0);
        responseDTO.setFinalCost(400.0);
        responseDTO.setExtraExpenses(100.0);
        responseDTO.setFinalWeight(500.0);
        responseDTO.setMarginFactor(1.5);
        responseDTO.setCreationDate(LocalDate.now());
        responseDTO.setFinishDate(LocalDate.now().plusDays(1));
        responseDTO.setProductId(1L);
        when(ordersMapper.toDto(order)).thenReturn(responseDTO);

        ResponseEntity<?> response = ordersController.createOrder(requestDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
    }

    @Test
    void testGetOrderById() {
        Orders order = new Orders();
        order.setId(1L);

        OrdersResponseDTO responseDTO = new OrdersResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setName("orderName");
        responseDTO.setDescription("orderDescription");
        responseDTO.setStatus(OrderStatus.NOT_STARTED);
        responseDTO.setCostPrice(200.0);
        responseDTO.setFinalCost(400.0);
        responseDTO.setExtraExpenses(100.0);
        responseDTO.setFinalWeight(500.0);
        responseDTO.setMarginFactor(1.5);
        responseDTO.setCreationDate(LocalDate.now());
        responseDTO.setFinishDate(LocalDate.now().plusDays(1));
        responseDTO.setProductId(1L);

        when(ordersService.findById(1L)).thenReturn(order);
        when(ordersMapper.toDto(order)).thenReturn(responseDTO);

        ResponseEntity<OrdersResponseDTO> response = ordersController.getOrderById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
    }

    @Test
    void testUpdateOrder() {
        OrdersRequestDTO requestDTO = new OrdersRequestDTO();
        requestDTO.setProductId(1L);
        requestDTO.setFinalWeight(500);
        requestDTO.setExtraExpenses(100);
        requestDTO.setMarginFactor(1.5);

        Products product = new Products();
        Users user = new Users();
        user.setId(1L);

        Orders updatedOrder = new Orders();
        updatedOrder.setId(1L);
        updatedOrder.setCostPrice(200);
        updatedOrder.setFinalCost(400);
        updatedOrder.setUser(user);

        when(productsService.findById(1L)).thenReturn(product);
        when(costCounter.countCost(product, 500, 100)).thenReturn(200.0);
        when(costCounter.countFinalCost(200.0, 1.5, 100)).thenReturn(400.0);
        when(ordersMapper.toEntity(requestDTO, product)).thenReturn(updatedOrder);
        when(ordersService.update(anyLong(), any())).thenReturn(updatedOrder);

        OrdersResponseDTO responseDTO = new OrdersResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setName("orderName");
        responseDTO.setDescription("orderDescription");
        responseDTO.setStatus(OrderStatus.NOT_STARTED);
        responseDTO.setCostPrice(200.0);
        responseDTO.setFinalCost(400.0);
        responseDTO.setExtraExpenses(100.0);
        responseDTO.setFinalWeight(500.0);
        responseDTO.setMarginFactor(1.5);
        responseDTO.setCreationDate(LocalDate.now());
        responseDTO.setFinishDate(LocalDate.now().plusDays(1));
        responseDTO.setProductId(1L);

        when(ordersMapper.toDto(updatedOrder)).thenReturn(responseDTO);

        ResponseEntity<?> response = ordersController.updateOrder(1L, requestDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
    }

    @Test
    void testFindAllOrders() {
        Users user = new Users();
        user.setId(1L);

        List<Orders> ordersList = new ArrayList<>();
        Orders order = new Orders();
        order.setId(1L);
        ordersList.add(order);

        List<OrdersResponseDTO> responseDTOList = new ArrayList<>();
        OrdersResponseDTO responseDTO = new OrdersResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setName("orderName");
        responseDTO.setDescription("orderDescription");
        responseDTO.setStatus(OrderStatus.NOT_STARTED);
        responseDTO.setCostPrice(200.0);
        responseDTO.setFinalCost(400.0);
        responseDTO.setExtraExpenses(100.0);
        responseDTO.setFinalWeight(500.0);
        responseDTO.setMarginFactor(1.5);
        responseDTO.setCreationDate(LocalDate.now());
        responseDTO.setFinishDate(LocalDate.now().plusDays(1));
        responseDTO.setProductId(1L);
        responseDTOList.add(responseDTO);

        when(ordersService.findAllByUserId(user.getId())).thenReturn(ordersList);
        when(ordersMapper.toDto(order)).thenReturn(responseDTO);

        ResponseEntity<List<OrdersResponseDTO>> response = ordersController.findAllOrders();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDTOList, response.getBody());
    }

    @Test
    void testDeleteOrderById() {
        doNothing().when(ordersService).delete(1L);

        ResponseEntity<?> response = ordersController.deleteOrderById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testSetOrderStatus() {
        doNothing().when(ordersService).updateOrderStatus(1L, OrderStatus.IN_PROCESS);

        ResponseEntity<?> response = ordersController.setOrderStatus(1L, "IN_PROCESS");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
