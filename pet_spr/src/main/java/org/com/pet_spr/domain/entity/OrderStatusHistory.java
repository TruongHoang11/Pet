package org.com.pet_spr.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.com.pet_spr.constant.OrderStatus;
import org.com.pet_spr.domain.dto.common.UserDateAuditing;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_order_status_history")
@Getter
@Setter
public class OrderStatusHistory extends UserDateAuditing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnore // Chặn quét ngược lại Order
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OrderStatus status; // Hoặc dùng Enum OrderStatus

    private String note;

    @Column(name = "changed_at")
    private LocalDateTime changedAt = LocalDateTime.now();
}