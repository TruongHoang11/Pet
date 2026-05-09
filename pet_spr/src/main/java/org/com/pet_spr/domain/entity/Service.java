package org.com.pet_spr.domain.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.com.pet_spr.domain.dto.common.FlagUserDateAuditing;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name ="tbl_services")
@NoArgsConstructor
@AllArgsConstructor
public class Service extends FlagUserDateAuditing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name ="description")
    private String description;

    @Column(name ="base_price",precision = 10, scale = 2)
    private BigDecimal basePrice;


    @Column(name = "duration_min")
    private int durationMin;

    @ManyToMany(mappedBy = "services")
    @JsonIgnore
    private List<Booking> booking;
}
