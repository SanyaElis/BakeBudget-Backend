package cs.vsu.ru.tpbakebudget.dto.response.outgoings;

import lombok.Data;

@Data
public class OutgoingsResponseDTO {
    private Long id;
    private String name;
    private double cost;
}
