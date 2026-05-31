package org.com.pet_spr.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.pet_spr.base.RestApiV1;
import org.com.pet_spr.base.VsResponseUtil;
import org.com.pet_spr.constant.UrlConstant;
import org.com.pet_spr.domain.dto.request.ReqAddCartItem;
import org.com.pet_spr.domain.dto.request.ReqUpdateCartItem;
import org.com.pet_spr.domain.dto.response.CartItemDto;
import org.com.pet_spr.domain.dto.response.CommonResponseDto;
import org.com.pet_spr.service.CartItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestApiV1
@Slf4j
@RequiredArgsConstructor
public class CartItemController {
    private final CartItemService cartItemService;


    @PostMapping(UrlConstant.CartItem.ADD_CART_ITEM)
    public ResponseEntity<?> addCartItem(@RequestBody @Valid ReqAddCartItem reqAddCartItem)  {
        CartItemDto cartItemDto = cartItemService.addCartItem(reqAddCartItem);
        return VsResponseUtil.success(HttpStatus.OK, cartItemDto);
    }

    @PutMapping(UrlConstant.CartItem.UPDATE_CART_ITEM)
    public ResponseEntity<?> updateCartItem(@RequestBody @Valid ReqUpdateCartItem reqUpdateCartItem) {
        return VsResponseUtil.success(HttpStatus.OK, cartItemService.updateCartItem(reqUpdateCartItem));

    }

    @DeleteMapping(UrlConstant.CartItem.DELETE_CART_ITEM)
    public ResponseEntity<?> deleteCartItem(@PathVariable Long id){
        CommonResponseDto commonResponseDto = cartItemService.deleteCartItem(id);

        return VsResponseUtil.success(HttpStatus.OK, commonResponseDto);

    }

}
