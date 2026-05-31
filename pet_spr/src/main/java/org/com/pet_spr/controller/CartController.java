package org.com.pet_spr.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.pet_spr.base.RestApiV1;
import org.com.pet_spr.base.VsResponseUtil;
import org.com.pet_spr.constant.UrlConstant;
import org.com.pet_spr.domain.dto.request.ReqAddCartItem;
import org.com.pet_spr.domain.dto.request.ReqUpdateCartItem;
import org.com.pet_spr.domain.dto.response.CartDto;
import org.com.pet_spr.domain.dto.response.CartItemDto;
import org.com.pet_spr.domain.dto.response.CommonResponseDto;
import org.com.pet_spr.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestApiV1
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @GetMapping(UrlConstant.Cart.GET_CART)
    public ResponseEntity<?> getCard()  {
      CartDto cartDto = cartService.getCart();
        return VsResponseUtil.success(HttpStatus.OK, cartDto);
    }


    @DeleteMapping(UrlConstant.Cart.DELETE_CART)
    public ResponseEntity<?> deleteCartItem(){
        CommonResponseDto commonResponseDto = cartService.deleteCart();
        return VsResponseUtil.success(HttpStatus.OK, commonResponseDto);

    }

}
