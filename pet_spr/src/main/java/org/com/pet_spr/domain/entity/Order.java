package org.com.pet_spr.domain.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.com.pet_spr.constant.StatusEnum;
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StatusEnum status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "order")
    private List<OrderStatusHistory> statusHistory;


    @OneToMany(mappedBy = "order")
    private List<OrderDetail> orderDetails;

    @OneToOne(mappedBy = "order")
    private Payment payment;


}
