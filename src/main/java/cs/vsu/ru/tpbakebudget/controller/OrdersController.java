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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@SecurityRequirement(name = "bearerAuth")
public class OrdersController {

    private final OrdersServiceImpl ordersService;

    private final ProductsServiceImpl productsService;

    private final CalculationMapper calculationMapper;

    private final OrdersMapper ordersMapper;

    private final CostCounter costCounter;

    @Autowired
    public OrdersController(OrdersServiceImpl ordersService, ProductsServiceImpl productsService, CalculationMapper calculationMapper, OrdersMapper ordersMapper, CostCounter costCounter) {
        this.ordersService = ordersService;
        this.productsService = productsService;
        this.calculationMapper = calculationMapper;
        this.ordersMapper = ordersMapper;
        this.costCounter = costCounter;
    }

    @PostMapping("/calculate")
    @Operation(summary = "Calculate cost price of order", description = "Create a new calculation.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Calculated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Invalid input: Please provide valid data"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Unauthorized"))),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Product not found"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Internal server error")))
    })
    public ResponseEntity<CalculationResponseDTO> calculate(@Valid @RequestBody CalculationRequestDTO calculationRequestDTO) {
        Products product = productsService.findById(calculationRequestDTO.getProductId());

        double costPrice = costCounter.countCost(product, calculationRequestDTO.getFinalWeight(), calculationRequestDTO.getExtraExpenses());
        double finalCost = costCounter.countFinalCost(costPrice, calculationRequestDTO.getMarginFactor(), calculationRequestDTO.getExtraExpenses());

        return new ResponseEntity<>(calculationMapper.toDto(costPrice, finalCost), HttpStatus.OK);
    }

    @PostMapping("/create")
    @Operation(summary = "Create a new Order", description = "Create a new Order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Invalid input: Please provide valid data"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Unauthorized"))),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Product not found"))),
            @ApiResponse(responseCode = "409", description = "Conflict - Order with this name already exists",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Conflict: Order with this name already exists"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Internal server error")))
    })
    public ResponseEntity<?> createOrder(@Valid @RequestBody OrdersRequestDTO ordersRequestDTO) {
        Users users = (Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Products product = productsService.findById(ordersRequestDTO.getProductId());
        double costPrice = costCounter.countCost(product, ordersRequestDTO.getFinalWeight(), ordersRequestDTO.getExtraExpenses());
        double finalCost = costCounter.countFinalCost(costPrice, ordersRequestDTO.getMarginFactor(), ordersRequestDTO.getExtraExpenses());
        Orders order = ordersMapper.toEntity(ordersRequestDTO, product);
        order.setCostPrice(costPrice);
        order.setFinalCost(finalCost);
        order.setUser(users);
        Orders savedOrder = ordersService.save(order);
        if (savedOrder != null) {
            return new ResponseEntity<>(ordersMapper.toDto(savedOrder), HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>("Order with this name already exists", HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/get/{id}")
    @Operation(summary = "Get Order by ID", description = "Get an order by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order successfully found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Unauthorized"))),
            @ApiResponse(responseCode = "404", description = "Order not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Order not found"))),
            @ApiResponse(responseCode = "409", description = "Conflict - Order with this name already exists",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Conflict: Order with this name already exists"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Internal server error")))
    })
    public ResponseEntity<OrdersResponseDTO> getOrderById(@PathVariable Long id) {
        Orders order = ordersService.findById(id);
        return ResponseEntity.ok(ordersMapper.toDto(order));
    }

    @PutMapping("/update/{id}")
    @Operation(summary = "Update Order", description = "Update an existing order by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Invalid input: Please provide valid data"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Unauthorized"))),
            @ApiResponse(responseCode = "404", description = "Order not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Order not found"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Internal server error")))
    })
    public ResponseEntity<?> updateOrder(@PathVariable Long id, @Valid @RequestBody OrdersRequestDTO updatedOrderDTO) {
        Users users = (Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Products product = productsService.findById(updatedOrderDTO.getProductId());
        double costPrice = costCounter.countCost(product, updatedOrderDTO.getFinalWeight(), updatedOrderDTO.getExtraExpenses());
        Orders newOrder = ordersMapper.toEntity(updatedOrderDTO, product);
        newOrder.setCostPrice(costPrice);
        double finalCost = costCounter.countFinalCost(newOrder.getCostPrice(), newOrder.getMarginFactor(), newOrder.getExtraExpenses());
        newOrder.setFinalCost(finalCost);
        newOrder.setUser(users);
        Orders updatedOrder = ordersService.update(id, newOrder);

        if (updatedOrder != null) {
            return new ResponseEntity<>(ordersMapper.toDto(updatedOrder), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Order with this name already exists", HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/findAll")
    @Operation(summary = "Find All Orders", description = "Get a list of all orders.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of orders retrieved successfully"),
            @ApiResponse(responseCode = "204", description = "No content found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "No content found"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Unauthorized"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Internal server error")))
    })
    public ResponseEntity<List<OrdersResponseDTO>> findAllOrders() {
        Users users = (Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Orders> orders = ordersService.findAllByUserId(users.getId());
        List<OrdersResponseDTO> ordersResponseDTOS = new ArrayList<>();

        if (orders.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        for (Orders order : orders) {
            ordersResponseDTOS.add(ordersMapper.toDto(order));
        }
        return new ResponseEntity<>(ordersResponseDTOS, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Delete Order", description = "Delete an order by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Unauthorized"))),
            @ApiResponse(responseCode = "404", description = "Order not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Order not found"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Internal server error")))
    })
    public ResponseEntity<?> deleteOrderById(@PathVariable Long id) {
        ordersService.delete(id);
        return ResponseEntity.ok("Order deleted successfully");
    }

    @PutMapping("/setStatus/{id}")
    @Operation(summary = "Set Order status", description = "Set a status of order by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status updated successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Unauthorized"))),
            @ApiResponse(responseCode = "404", description = "Order not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Order not found"))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Unsupported status"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Internal server error")))
    })
    public ResponseEntity<?> setOrderStatus(@PathVariable Long id, @Parameter(example = "IN_PROCESS") @RequestParam String status) {
        if (EnumUtils.isValidEnum(OrderStatus.class, status)) {
            OrderStatus orderStatus = OrderStatus.valueOf(status);
            ordersService.updateOrderStatus(id, orderStatus);
            return ResponseEntity.ok("Status updated successfully");
        } else {
            throw new BadCredentialsException("Unsupported status");
        }
    }
}
