package cs.vsu.ru.tpbakebudget.mapper;

import cs.vsu.ru.tpbakebudget.dto.request.calculation.CalculationRequestDTO;
import cs.vsu.ru.tpbakebudget.dto.responce.calculation.CalculationResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class CalculationMapper {
    public CalculationResponseDTO toDto(CalculationRequestDTO calculationRequestDTO, double costPrice) {
        CalculationResponseDTO dto = new CalculationResponseDTO();
        dto.setFinalWeight(calculationRequestDTO.getFinalWeight());
        dto.setExtraExpenses(calculationRequestDTO.getExtraExpenses());
        dto.setCostPrice(costPrice);
        return dto;
    }
}
