package org.com.pet_spr.repository;

import org.com.pet_spr.constant.UrlConstant;
import org.com.pet_spr.domain.entity.ShippingAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShippingAddressRepository extends JpaRepository<ShippingAddress, Long>, JpaSpecificationExecutor<ShippingAddress> {
    long countByUserId(String userId);

    Optional<ShippingAddress> findByUserIdAndIsDefaultTrue(String userId);

    List<ShippingAddress> findByUserId(String userId);
}
