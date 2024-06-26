package cs.vsu.ru.tpbakebudget.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cs.vsu.ru.tpbakebudget.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private double costPrice;

    private double finalCost;

    private double extraExpenses;

    @Column(nullable = false)
    private double finalWeight;

    @Column(nullable = false)
    private double marginFactor;

    @Temporal(TemporalType.DATE)
    private LocalDate creationDate;

    @Temporal(TemporalType.DATE)
    private LocalDate finishDate;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private Users user;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnore
    private Products product;

    public Orders(String name, String description, OrderStatus status, double costPrice, double finalCost, double extraExpenses, double finalWeight, double marginFactor, LocalDate creationDate, LocalDate finishDate, Users user, Products product) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.costPrice = costPrice;
        this.finalCost = finalCost;
        this.extraExpenses = extraExpenses;
        this.finalWeight = finalWeight;
        this.marginFactor = marginFactor;
        this.creationDate = creationDate;
        this.finishDate = finishDate;
        this.user = user;
        this.product = product;
    }

    public Orders() {
    }
}
