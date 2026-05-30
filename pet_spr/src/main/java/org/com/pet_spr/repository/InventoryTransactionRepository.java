package org.com.pet_spr.repository;

import org.com.pet_spr.domain.entity.InventoryTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Long>, JpaSpecificationExecutor<InventoryTransaction> {

    InventoryTransaction findByInventoryId(Long inventoryId);

    List<InventoryTransaction> findByInventoryIdOrderByCreatedDateDesc(Long inventoryId);
}
