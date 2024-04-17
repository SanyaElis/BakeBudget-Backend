package cs.vsu.ru.tpbakebudget.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Ingredients {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private double weight;

    @Column(nullable = false)
    private double cost;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true)
    private Users user;

    @OneToMany(mappedBy = "pk.ingredient", cascade = CascadeType.ALL)
    private List<IngredientsInProduct> ingredientsInProduct;

    public Ingredients(Long id, String name, double weight, double cost) {
        this.id = id;
        this.name = name;
        this.weight = weight;
        this.cost = cost;
    }

    public Ingredients(String name, double weight, double cost, Users user) {
        this.name = name;
        this.weight = weight;
        this.cost = cost;
        this.user = user;
    }

    public Ingredients(String name, double weight, double cost) {
        this.name = name;
        this.weight = weight;
        this.cost = cost;
    }

    public Ingredients() {

    }
}
