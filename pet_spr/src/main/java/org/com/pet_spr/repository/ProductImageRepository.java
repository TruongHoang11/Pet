package org.com.pet_spr.repository;

import org.com.pet_spr.domain.entity.Product;
import org.com.pet_spr.domain.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long>, JpaSpecificationExecutor<Product> {
}
