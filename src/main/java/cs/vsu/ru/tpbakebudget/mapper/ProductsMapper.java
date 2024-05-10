package cs.vsu.ru.tpbakebudget.mapper;

import cs.vsu.ru.tpbakebudget.dto.request.products.ProductsRequestDTO;
import cs.vsu.ru.tpbakebudget.dto.responce.products.ProductsResponseDTO;
import cs.vsu.ru.tpbakebudget.model.Products;
import cs.vsu.ru.tpbakebudget.model.Users;
import org.springframework.stereotype.Component;

@Component
public class ProductsMapper {
    public ProductsResponseDTO toDto(Products entity) {
        if (entity == null) {
            return null;
        }
        ProductsResponseDTO dto = new ProductsResponseDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setWeight(entity.getWeight());
        return dto;
    }

    public Products toEntity(ProductsRequestDTO dto, Users user) {
        if (dto == null) {
            return null;
        }
        Products entity = new Products();
        entity.setName(dto.getName());
        entity.setWeight(dto.getWeight());
        entity.setUser(user);
        return entity;
    }
}
