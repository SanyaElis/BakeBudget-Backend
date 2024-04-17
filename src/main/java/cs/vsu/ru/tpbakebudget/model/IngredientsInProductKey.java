package cs.vsu.ru.tpbakebudget.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.io.Serializable;

@Embeddable
@Data
public class IngredientsInProductKey implements Serializable {

    @ManyToOne
    @JoinColumn(name = "ingredient_id", nullable = true)
    private Ingredients ingredient;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Products product;

    public IngredientsInProductKey(Ingredients ingredient, Products product) {
        this.ingredient = ingredient;
        this.product = product;
    }

    public IngredientsInProductKey() {
    }
}
