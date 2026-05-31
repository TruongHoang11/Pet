package org.com.pet_spr.domain.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.com.pet_spr.constant.ErrorMessage;

@Getter
@Setter
public class ReqUpdateCartItem {

    @NotNull(message = ErrorMessage.NOT_NULL_FIELD)
    private Long itemId;

    @NotNull(message = ErrorMessage.NOT_NULL_FIELD)
    @Min(value = 0, message = "Số lượng tối thiểu là 1")
    private Integer quantity; // nếu quantity = 0 -> xoá item khỏi giỏ hàng
}
