package org.com.pet_spr.domain.entity;




import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.aspectj.weaver.ast.Or;
import org.com.pet_spr.constant.GenderEnum;
import org.com.pet_spr.domain.dto.common.UserDateAuditing;
import org.hibernate.annotations.UuidGenerator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "tbl_users")
public class User extends UserDateAuditing implements Serializable {

    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private String id;


    @Column(name = "name")
    private String name;

    @NotBlank(message = "email khong duoc de trong")
    @Column(name = "email")
    private String email;

    @NotBlank(message = "password khong duoc de trong")
    @Column(name = "password")
    private String password;

    @Column(name = "age")
    private Integer age;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private GenderEnum gender;

    @Column(name = "address")
    private String address;


    @Column(name ="provider")
    private String provider; // GOOGLE or FACEBOOK

    @Column(name = "provider_id")
    private String providerId; // ID from the provider

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Pet> pets;


    @OneToMany(mappedBy = "user", fetch =  FetchType.LAZY)
    @JsonIgnore
    private List<Booking> bookings;


    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Order> orders;


    // Quan hệ 1-N với CartItem
    // mappedBy = "user" là tên biến 'user' bạn đặt trong lớp CartItem
    // orphanRemoval = true giúp tự động xóa CartItem khỏi DB nếu bạn xóa nó khỏi list này
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @OneToMany(mappedBy = "user")
    private List<ShippingAddress> shippingAddresses;

    @OneToMany(mappedBy = "user")
    private List<Review> reviews;

}
