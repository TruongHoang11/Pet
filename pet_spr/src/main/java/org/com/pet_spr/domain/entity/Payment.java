package org.com.pet_spr.domain.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.com.pet_spr.constant.PaymentStatus;

import java.math.BigDecimal;

@Entity
@Table(name = "tbl_payments")
@Getter
@Setter
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private String paymentMethod; // VD: MOMO, VNPAY, CASH

    @Column(name = "transaction_id", unique = true)
    private String transactionId; // Lưu mã từ bên thứ 3 trả về

    @Column(name = "amount", precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PaymentStatus status; // SUCCESS, FAILED, PENDING

}
