package cs.vsu.ru.tpbakebudget.mapper;

import cs.vsu.ru.tpbakebudget.dto.response.calculation.CalculationResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class CalculationMapper {
    public CalculationResponseDTO toDto(double costPrice, double finalCost) {
        CalculationResponseDTO dto = new CalculationResponseDTO();
        dto.setCostPrice(costPrice);
        dto.setFinalCost(finalCost);
        return dto;
    }
}
