package org.com.pet_spr.domain.mapper;

import org.com.pet_spr.domain.dto.response.InventoryTransactionDto;
import org.com.pet_spr.domain.entity.InventoryTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InventoryTransactionMapper {
    @Mapping(target = "currentStock", source = "inventory.quantity")
    InventoryTransactionDto toInventoryTransactionDto(InventoryTransaction inventoryTransaction);

    List<InventoryTransactionDto> toListInventoryTransaction(List<InventoryTransaction> inventoryTransactions);

}
