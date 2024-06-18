package cs.vsu.ru.tpbakebudget.mapper;

import cs.vsu.ru.tpbakebudget.dto.request.orders.OrdersRequestDTO;
import cs.vsu.ru.tpbakebudget.dto.response.orders.OrdersResponseDTO;
import cs.vsu.ru.tpbakebudget.enums.OrderStatus;
import cs.vsu.ru.tpbakebudget.model.Orders;
import cs.vsu.ru.tpbakebudget.model.Products;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class OrdersMapper {
    public OrdersResponseDTO toDto(Orders entity) {
        if (entity == null) {
            return null;
        }
        OrdersResponseDTO dto = new OrdersResponseDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setStatus(entity.getStatus());
        dto.setCostPrice(entity.getCostPrice());
        dto.setFinalCost(entity.getFinalCost());
        dto.setExtraExpenses(entity.getExtraExpenses());
        dto.setFinalWeight(entity.getFinalWeight());
        dto.setMarginFactor(entity.getMarginFactor());
        dto.setCreationDate(entity.getCreationDate());
        dto.setFinishDate(entity.getFinishDate());
        dto.setProductId(entity.getProduct().getId());
        return dto;
    }

    public Orders toEntity(OrdersRequestDTO dto, Products product) {
        if (dto == null) {
            return null;
        }
        Orders entity = new Orders();
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setStatus(OrderStatus.NOT_STARTED);
        entity.setExtraExpenses(dto.getExtraExpenses());
        entity.setFinalWeight(dto.getFinalWeight());
        entity.setMarginFactor(dto.getMarginFactor());
        entity.setCreationDate(LocalDate.now());
        entity.setProduct(product);
        return entity;
    }
}
