package cs.vsu.ru.tpbakebudget.component;

import cs.vsu.ru.tpbakebudget.model.Ingredients;
import cs.vsu.ru.tpbakebudget.model.IngredientsInProduct;
import cs.vsu.ru.tpbakebudget.model.Outgoings;
import cs.vsu.ru.tpbakebudget.model.Products;
import cs.vsu.ru.tpbakebudget.service.impl.IngredientsInProductServiceImpl;
import cs.vsu.ru.tpbakebudget.service.impl.IngredientsServiceImpl;
import cs.vsu.ru.tpbakebudget.service.impl.OutgoingsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CostCounter {
    private final OutgoingsServiceImpl outgoingsService;

    private final IngredientsInProductServiceImpl ingredientsInProductService;

    private final IngredientsServiceImpl ingredientsService;

    @Autowired
    public CostCounter(OutgoingsServiceImpl outgoingsService, IngredientsInProductServiceImpl ingredientsInProductService, IngredientsServiceImpl ingredientsService) {
        this.outgoingsService = outgoingsService;
        this.ingredientsInProductService = ingredientsInProductService;
        this.ingredientsService = ingredientsService;
    }

    public double countCost(Products product, double finalWeight) {
        List<Outgoings> outgoings = outgoingsService.findAllByProductId(product.getId());
        List<IngredientsInProduct> ingredientsInProduct = ingredientsInProductService.findByPk_ProductId(product.getId());

        double result = 0;
        double ingredients_coefficient;
        for (IngredientsInProduct inProduct : ingredientsInProduct) {
            Ingredients ingredient = ingredientsService.findById(inProduct.getPk().getIngredient().getId());
            ingredients_coefficient = inProduct.getWeight() / ingredient.getWeight();
            result += ingredient.getCost() * ingredients_coefficient;
        }

        double outgoings_coefficient = finalWeight / product.getWeight();
        for (Outgoings outgoing : outgoings) {
            result += outgoing.getCost() * outgoings_coefficient;
        }

        return result;
    }

    public double countFinalCost(double costPrice, double marginFactor, double extraExpenses){
        return costPrice * marginFactor + extraExpenses;
    }
}
