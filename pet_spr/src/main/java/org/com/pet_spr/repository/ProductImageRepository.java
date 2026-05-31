package org.com.pet_spr.repository;

import org.com.pet_spr.domain.entity.Product;
import org.com.pet_spr.domain.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long>, JpaSpecificationExecutor<Product> {
    List<ProductImage> findByProductId(Long productId);

    boolean existsByProductIdAndIsMainTrue(Long productId);

    // Hủy trạng thái ảnh chính của toàn bộ ảnh thuộc sản phẩm này
    @Modifying
    @Query("UPDATE ProductImage p SET p.isMain = false WHERE p.product.id = :productId")
    void resetMainImageByProductId(Long productId);
}
