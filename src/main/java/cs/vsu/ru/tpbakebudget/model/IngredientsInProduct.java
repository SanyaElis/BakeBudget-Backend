package cs.vsu.ru.tpbakebudget.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.Data;

import java.io.Serializable;

@Entity
@Data
public class IngredientsInProduct implements Serializable {

    @EmbeddedId
    private IngredientsInProductKey pk;

    @Column(nullable = false)
    private double weight;

    public IngredientsInProduct(IngredientsInProductKey pk, double weight) {
        this.pk = pk;
        this.weight = weight;
    }

    public IngredientsInProduct(double weight) {
        this.weight = weight;
    }

    public IngredientsInProduct() {
    }
}
