package org.com.pet_spr.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.com.pet_spr.domain.dto.common.DateAuditing;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "tbl_categories")
public class Category extends DateAuditing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "menu_id")
    private Menu menu; // Kết nối Category với Menu


    @OneToMany(mappedBy = "category")
    @JsonIgnore
    private List<Product> products;
}