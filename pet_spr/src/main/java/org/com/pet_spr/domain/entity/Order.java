package org.com.pet_spr.domain.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.com.pet_spr.constant.OrderStatus;
import org.com.pet_spr.domain.dto.common.UserDateAuditing;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_orders")
public class Order extends UserDateAuditing {
    // đặt hàng
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "shipping_name")
    private String shippingName; // tên người nhận
    @Column(name = "shipping_phone")
    private String shippingPhone;
    @Column(name = "shipping_address_full", columnDefinition = "TEXT")
    private String shippingAddressFull;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OrderStatus status; //PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL) // Thêm cascade cho Payment
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderStatusHistory> statusHistory;


    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> orderDetails;


}
