package org.com.pet_spr.domain.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.com.pet_spr.domain.dto.common.DateAuditing;

import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_menus")
public class Menu extends DateAuditing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "category_url")
    private String categoryUrl;

    private Integer sortOrder;

    private boolean isActive;

    @ManyToOne //NHIỀU menu con thuộc về MỘT menu cha
    @JoinColumn(name = "parent_id")
    private Menu parent;

    @OneToMany(mappedBy = "parent") //MỘT category cha có NHIỀU category con
    @OrderBy("sortOrder ASC") // Tự động sắp xếp các con khi lấy ra
    private List<Menu> children; // Danh sách các category con


    @OneToMany(mappedBy = "menu")
    @JsonIgnore
    private List<Category> categories;

}
