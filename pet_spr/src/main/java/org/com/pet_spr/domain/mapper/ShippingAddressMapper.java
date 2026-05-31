package org.com.pet_spr.domain.mapper;


import org.com.pet_spr.domain.dto.response.ShippingAddressDto;
import org.com.pet_spr.domain.entity.ShippingAddress;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ShippingAddressMapper {

    @Mapping(target="fullAddress", expression = "java(buildFullAddress(shippingAddress))")
    ShippingAddressDto toDto(ShippingAddress shippingAddress);

    default String buildFullAddress(ShippingAddress dto){
        return dto.getAddressDetail() + ", " + dto.getWard() + ", " + dto.getDistrict() + ", " + dto.getProvince();
    }
}
