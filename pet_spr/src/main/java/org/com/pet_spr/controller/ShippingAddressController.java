package org.com.pet_spr.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.pet_spr.base.RestApiV1;
import org.com.pet_spr.base.VsResponseUtil;
import org.com.pet_spr.constant.UrlConstant;
import org.com.pet_spr.domain.dto.request.ReqCreateProduct;
import org.com.pet_spr.domain.dto.request.ReqCreateShippingAddress;
import org.com.pet_spr.domain.dto.request.ReqUpdateProduct;
import org.com.pet_spr.domain.dto.request.ReqUpdateShippingAddress;
import org.com.pet_spr.domain.dto.response.CommonResponseDto;
import org.com.pet_spr.domain.dto.response.ProductDto;
import org.com.pet_spr.domain.dto.response.ShippingAddressDto;
import org.com.pet_spr.service.ShippingAddressService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestApiV1
@RequiredArgsConstructor
@Slf4j
public class ShippingAddressController {
    private final ShippingAddressService service;


    @PatchMapping(UrlConstant.ShippingAddress.SET_DEFAULT_ADDRESS)
    public ResponseEntity<?> setDefaultShippingAddress(@PathVariable Long id){
        return VsResponseUtil.success(HttpStatus.OK,service.setDefaultShippingAddress(id));

    }

    @PostMapping(UrlConstant.ShippingAddress.CREATE_SHIPPING_ADDRESS)
    public ResponseEntity<?> createShippingAddress(@RequestBody @Valid ReqCreateShippingAddress req)  {
        ShippingAddressDto dto = service.createShippingAddress(req);
        return VsResponseUtil.success(HttpStatus.OK, dto);
    }

    @PutMapping(UrlConstant.ShippingAddress.UPDATE_SHIPPING_ADDRESS)
    public ResponseEntity<?> updateShippingAddress(@RequestBody @Valid ReqUpdateShippingAddress rq) {
        return VsResponseUtil.success(HttpStatus.OK,service.updateShippingAddress(rq) );

    }


    @GetMapping(UrlConstant.ShippingAddress.GET_SHIPPING_ADDRESSES)
    public ResponseEntity<?> getAllShippingAddress(){
        return VsResponseUtil.success(HttpStatus.OK,service.getAllShippingAddress());

    }

}
