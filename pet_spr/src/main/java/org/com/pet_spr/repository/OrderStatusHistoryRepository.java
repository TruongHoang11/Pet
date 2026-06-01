package org.com.pet_spr.repository;

import org.com.pet_spr.domain.entity.OrderStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory, Long>, JpaSpecificationExecutor<OrderStatusHistory> {
}
