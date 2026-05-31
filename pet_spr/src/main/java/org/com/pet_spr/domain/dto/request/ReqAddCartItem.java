package org.com.pet_spr.domain.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqAddCartItem {
    private Long productId;

    private Integer quantity; //quantity > 0
}
