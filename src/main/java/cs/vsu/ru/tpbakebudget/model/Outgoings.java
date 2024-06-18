package cs.vsu.ru.tpbakebudget.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Outgoings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private double cost;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonIgnore
    private Products product;

    public Outgoings(Long id, String name, double cost, Products product) {
        this.id = id;
        this.name = name;
        this.cost = cost;
        this.product = product;
    }

    public Outgoings(String name, double cost, Products product) {
        this.name = name;
        this.cost = cost;
        this.product = product;
    }

    public Outgoings() {
    }
}
