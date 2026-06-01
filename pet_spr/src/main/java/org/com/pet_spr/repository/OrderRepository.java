package org.com.pet_spr.repository;

import org.com.pet_spr.constant.OrderStatus;
import org.com.pet_spr.domain.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    Page<Order> findByUserIdAndStatus(String userId, OrderStatus status, Pageable pageable);

    Page<Order> findByUserId(String userId, Pageable pageable);
}