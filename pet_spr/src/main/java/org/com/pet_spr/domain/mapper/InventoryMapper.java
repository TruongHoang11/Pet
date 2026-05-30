package org.com.pet_spr.domain.mapper;

import org.com.pet_spr.constant.ErrorMessage;
import org.com.pet_spr.domain.dto.response.InventoryDto;
import org.com.pet_spr.domain.dto.response.InventoryTransactionDto;
import org.com.pet_spr.domain.entity.Inventory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InventoryMapper {

    @Mapping(target="productId", source = "product.id")
    @Mapping(target="productName", source = "product.name")
    @Mapping(target="productPrice", source = "product.price")
    InventoryDto toDto(Inventory inventory);
}
