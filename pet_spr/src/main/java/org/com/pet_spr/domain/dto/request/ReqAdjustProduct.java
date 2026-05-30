package org.com.pet_spr.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.com.pet_spr.constant.ErrorMessage;

@Getter
@Setter
public class ReqAdjustProduct {
    @NotNull(message = ErrorMessage.NOT_NULL_FIELD)
    private Long productId;

    @NotNull(message = ErrorMessage.NOT_NULL_FIELD)
    private Integer newQuantity; // số lượng thực tế sau khi kiểm kê

    @NotBlank(message = ErrorMessage.NOT_BLANK_FIELD)
    private String note;
}
