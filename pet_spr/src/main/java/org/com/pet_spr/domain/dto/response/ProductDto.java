package org.com.pet_spr.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.com.pet_spr.domain.dto.common.DateAuditing;

import java.math.BigDecimal;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto extends DateAuditing {

    private Long id;

    private String name;

    private String description;

    private BigDecimal price;

    private String categoryName;

    private Long categoryId;



    private Integer stockQuantity;
}
