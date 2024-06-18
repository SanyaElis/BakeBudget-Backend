package cs.vsu.ru.tpbakebudget.dto.response.orders;

import cs.vsu.ru.tpbakebudget.enums.OrderStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class OrdersResponseDTO {
    private Long id;

    private String name;

    private String description;

    private OrderStatus status;

    private double costPrice;

    private double finalCost;

    private double extraExpenses;

    private double finalWeight;

    private double marginFactor;

    private LocalDate creationDate;

    private LocalDate finishDate;

    private Long productId;
}

