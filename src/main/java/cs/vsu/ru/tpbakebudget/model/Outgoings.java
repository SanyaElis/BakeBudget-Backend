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
    @JoinColumn(name = "user_id", nullable = true)
    @JsonIgnore
    private Users user;

    public Outgoings(Long id, String name, double cost, Users user) {
        this.id = id;
        this.name = name;
        this.cost = cost;
        this.user = user;
    }

    public Outgoings(String name, double cost, Users user) {
        this.name = name;
        this.cost = cost;
        this.user = user;
    }

    public Outgoings() {
    }
}
