package org.com.pet_spr.service;

import org.com.pet_spr.domain.dto.request.ReqAddCartItem;
import org.com.pet_spr.domain.dto.request.ReqUpdateCartItem;
import org.com.pet_spr.domain.dto.response.CartItemDto;
import org.com.pet_spr.domain.dto.response.CommonResponseDto;

public interface CartItemService {

    CartItemDto addCartItem(ReqAddCartItem reqAddCartItem);

    CartItemDto updateCartItem(ReqUpdateCartItem req);

    CommonResponseDto deleteCartItem(Long itemId);
}
