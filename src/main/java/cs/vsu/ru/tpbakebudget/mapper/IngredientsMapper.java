package cs.vsu.ru.tpbakebudget.mapper;

import cs.vsu.ru.tpbakebudget.dto.request.ingredients.IngredientsRequestDTO;
import cs.vsu.ru.tpbakebudget.dto.response.ingredients.IngredientsResponseDTO;
import cs.vsu.ru.tpbakebudget.model.Ingredients;
import cs.vsu.ru.tpbakebudget.model.Users;
import org.springframework.stereotype.Component;

@Component
public class IngredientsMapper {

    public IngredientsResponseDTO toDto(Ingredients entity) {
        if (entity == null) {
            return null;
        }
        IngredientsResponseDTO dto = new IngredientsResponseDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setWeight(entity.getWeight());
        dto.setCost(entity.getCost());
        return dto;
    }

    public Ingredients toEntity(IngredientsRequestDTO dto, Users user) {
        if (dto == null) {
            return null;
        }
        Ingredients entity = new Ingredients();
        entity.setName(dto.getName());
        entity.setWeight(dto.getWeight());
        entity.setCost(dto.getCost());
        entity.setUser(user);
        return entity;
    }
}
