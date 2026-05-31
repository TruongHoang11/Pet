package org.com.pet_spr.domain.dto.request;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.com.pet_spr.constant.ErrorMessage;
import org.com.pet_spr.validator.annotation.ValidPhone;

@Getter
@Setter
public class ReqUpdateShippingAddress {

    @NotNull(message = ErrorMessage.NOT_NULL_FIELD)
    private Long id;

    @NotBlank(message = ErrorMessage.NOT_NULL_FIELD)
    @Size(max = 100, message = ErrorMessage.INVALID_TOO_LONG_FIELD)
    private String fullName;

    @NotBlank(message = ErrorMessage.NOT_NULL_FIELD)
    @ValidPhone
    private String phone;

    @NotBlank(message = ErrorMessage.NOT_NULL_FIELD)
    private String addressDetail;

    @NotBlank(message = ErrorMessage.NOT_NULL_FIELD)
    private String ward;

    @NotBlank(message = ErrorMessage.NOT_NULL_FIELD)
    private String district;

    @NotBlank(message = ErrorMessage.NOT_NULL_FIELD)
    private String province;


    @NotNull(message = ErrorMessage.NOT_NULL_FIELD)
    private Boolean isDefault;
}
