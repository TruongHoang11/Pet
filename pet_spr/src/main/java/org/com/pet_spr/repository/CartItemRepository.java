package org.com.pet_spr.repository;

import org.com.pet_spr.domain.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long>, JpaSpecificationExecutor<CartItem> {
    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);
}
