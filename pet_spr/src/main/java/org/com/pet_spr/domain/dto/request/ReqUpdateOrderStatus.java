package org.com.pet_spr.domain.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.com.pet_spr.constant.ErrorMessage;
import org.com.pet_spr.constant.OrderStatus;

@Getter
@Setter
public class ReqUpdateOrderStatus {
    @NotNull(message = ErrorMessage.NOT_NULL_FIELD)
    private Long orderId;

    @NotNull(message = ErrorMessage.NOT_NULL_FIELD)
    private OrderStatus status;

    private String note;
}
