package org.com.pet_spr.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "tbl_reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Người thực hiện đánh giá

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product; // Sản phẩm được đánh giá

    @Column(nullable = false)
    private Integer rating; // Điểm đánh giá (ví dụ: từ 1 đến 5 sao) , //sp dc danh gia khi da mua

    @Column(columnDefinition = "TEXT")
    private String comment; // Nội dung nhận xét của khách hàng
}
