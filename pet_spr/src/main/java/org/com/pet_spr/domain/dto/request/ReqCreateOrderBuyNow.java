package org.com.pet_spr.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.com.pet_spr.constant.ErrorMessage;

@Getter
@Setter
public class ReqCreateOrderBuyNow {

    @NotNull(message = ErrorMessage.NOT_NULL_FIELD)
    private Long productId;

    @NotNull(message = ErrorMessage.NOT_NULL_FIELD)
    private Integer quantity;

    @NotNull(message = ErrorMessage.NOT_NULL_FIELD)
    private Long addressId;

    @NotBlank(message = ErrorMessage.NOT_BLANK_FIELD)
    private String paymentMethod;
}
