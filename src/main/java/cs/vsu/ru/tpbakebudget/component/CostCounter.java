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

    public double countCost(Products product, double finalWeight, double extraExpenses) {
        List<Outgoings> outgoings = outgoingsService.findAllByProductId(product.getId());
        List<IngredientsInProduct> ingredientsInProduct = ingredientsInProductService.findByPk_ProductId(product.getId());
        List<Ingredients> ingredients = ingredientsService.findByIngredientsInProductPkProductId(product.getId());

        if(ingredientsInProduct.size() != ingredients.size()){
            return -1;
        }
        double result = 0;

        double ingredients_coefficient;
        for (int i = 0; i < ingredientsInProduct.size(); i++) {
            ingredients_coefficient = ingredientsInProduct.get(i).getWeight() / ingredients.get(i).getWeight();
            result += ingredients.get(i).getCost() * ingredients_coefficient;
        }

        double outgoings_coefficient = finalWeight / product.getWeight();
        for (Outgoings outgoing : outgoings) {
            result += outgoing.getCost() * outgoings_coefficient;
        }

        result += extraExpenses;

        return result;
    }
}
