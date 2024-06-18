package cs.vsu.ru.tpbakebudget.dto.response.orders;

import lombok.Data;

@Data
public class IncomeResponseDTO {
    private double cost;

    private double income;

    public IncomeResponseDTO(double cost, double income) {
        this.cost = cost;
        this.income = income;
    }
}
