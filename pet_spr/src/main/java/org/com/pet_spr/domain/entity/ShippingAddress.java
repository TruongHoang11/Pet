package org.com.pet_spr.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.com.pet_spr.domain.dto.common.UserDateAuditing;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "tbl_shipping_addresses")
public class ShippingAddress extends UserDateAuditing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Địa chỉ này thuộc về User nào

    @Column(nullable = false)
    private String fullName; // Tên người nhận (có thể khác tên chủ tài khoản)

    @Column(nullable = false)
    private String phone; // Số điện thoại nhận hàng

    @Column(nullable = false)
    private String address; // Số nhà, tên đường

    @Column(nullable = false)
    private String city; // Thành phố/Tỉnh

    @Column(name = "is_default")
    private Boolean isDefault = false; // Đánh dấu đây có phải địa chỉ mặc định không
}

