package org.com.pet_spr.service;

import org.com.pet_spr.domain.dto.request.ReqCreateShippingAddress;
import org.com.pet_spr.domain.dto.request.ReqUpdateShippingAddress;
import org.com.pet_spr.domain.dto.response.CommonResponseDto;
import org.com.pet_spr.domain.dto.response.ShippingAddressDto;

import java.util.List;

public interface ShippingAddressService {

    ShippingAddressDto createShippingAddress(ReqCreateShippingAddress req);

    ShippingAddressDto updateShippingAddress(ReqUpdateShippingAddress req);

    CommonResponseDto deleteShippingAddress(Long addressId);

    List<ShippingAddressDto> getAllShippingAddress();

    CommonResponseDto setDefaultShippingAddress(Long addressId);

}
