package org.com.pet_spr.domain.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShippingAddressDto {
    private Long id;
    private String fullName;
    private String phone;
    private String addressDetail;
    private String ward;
    private String district;
    private String province;
    private Boolean isDefault;
    private String fullAddress;
}
