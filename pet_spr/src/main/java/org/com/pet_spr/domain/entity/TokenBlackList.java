package org.com.pet_spr.domain.entity;


import jakarta.persistence.*;
import lombok.*;
import org.com.pet_spr.constant.CommonConstant;

import java.time.LocalDateTime;

@Entity
@Table(name="tbl_token_blacklist")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TokenBlackList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, name = "token_type")
    private String tokenType = CommonConstant.BEARER_TOKEN;

    @Column(columnDefinition = "TEXT", nullable = false, name = "token")
    private String token;


    @Column(name = "reason", nullable = false)
    private String reason; //Lý do token này bị đưa vào danh sách đen. Ví dụ: "LOGOUT", "PASSWORD_CHANGED", hoặc "ADMIN_REVOKED".

    @Column(name = "black_list_at", nullable = false)
    private LocalDateTime blackListAt; //Thời điểm chính xác token bị vô hiệu hóa.

    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt; // time het han token


    @PrePersist
    public void prePersist() {
        if (blackListAt == null) blackListAt = LocalDateTime.now();
        if (expiredAt == null) expiredAt = LocalDateTime.now().plusDays(1); // thuc te nen truyen vao thoi gian het han cua accessToken
    }
}
