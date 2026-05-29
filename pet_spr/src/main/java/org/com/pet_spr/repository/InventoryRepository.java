package org.com.pet_spr.repository;

import org.com.pet_spr.domain.entity.Inventory;
import org.com.pet_spr.domain.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InventoryRepository extends JpaRepository<Inventory, Long>, JpaSpecificationExecutor<Inventory> {
}
