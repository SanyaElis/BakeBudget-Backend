package cs.vsu.ru.tpbakebudget.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Products {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private double weight;

    private String minioPictureName;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private Users user;

    @OneToMany(mappedBy = "pk.product", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<IngredientsInProduct> ingredientsInProduct;

    public Products(Long id, String name, double weight, Users user) {
        this.id = id;
        this.name = name;
        this.weight = weight;
        this.user = user;
        this.minioPictureName = null;
    }

    public Products(String name, double weight, Users user) {
        this.name = name;
        this.weight = weight;
        this.user = user;
    }

    public Products() {
    }
}