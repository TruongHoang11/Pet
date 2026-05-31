package org.com.pet_spr.domain.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.com.pet_spr.constant.ErrorMessage;

@Getter
@Setter
public class ReqSetMainImage {
    @NotNull(message = ErrorMessage.NOT_NULL_FIELD)
    private Long productId;
    @NotNull(message = ErrorMessage.NOT_NULL_FIELD)
    private Long imageId;
}
