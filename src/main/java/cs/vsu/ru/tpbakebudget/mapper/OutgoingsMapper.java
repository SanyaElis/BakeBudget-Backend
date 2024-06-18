package cs.vsu.ru.tpbakebudget.mapper;

import cs.vsu.ru.tpbakebudget.dto.request.outgoings.OutgoingsRequestDTO;
import cs.vsu.ru.tpbakebudget.dto.response.outgoings.OutgoingsResponseDTO;
import cs.vsu.ru.tpbakebudget.model.Outgoings;
import cs.vsu.ru.tpbakebudget.model.Products;
import org.springframework.stereotype.Component;

@Component
public class OutgoingsMapper {

    public OutgoingsResponseDTO toDto(Outgoings entity) {
        if (entity == null) {
            return null;
        }
        OutgoingsResponseDTO dto = new OutgoingsResponseDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setCost(entity.getCost());
        return dto;
    }

    public Outgoings toEntity(OutgoingsRequestDTO dto, Products product) {
        if (dto == null) {
            return null;
        }
        Outgoings entity = new Outgoings();
        entity.setName(dto.getName());
        entity.setCost(dto.getCost());
        entity.setProduct(product);
        return entity;
    }
}
