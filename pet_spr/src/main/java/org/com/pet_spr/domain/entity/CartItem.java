package org.com.pet_spr.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.com.pet_spr.domain.dto.common.DateAuditing;
import org.com.pet_spr.domain.dto.common.FlagUserDateAuditing;

@Entity
@Table(name = "tbl_cart_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItem extends DateAuditing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    //// Nhiều item ở các giỏ hàng khác nhau có thể trỏ về cùng 1 Product
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    //// Nhiều item thuộc về cùng 1 giỏ hàng
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @Column(nullable = false)
    private Integer quantity;

}