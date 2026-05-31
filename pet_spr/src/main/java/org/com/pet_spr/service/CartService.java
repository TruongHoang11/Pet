package org.com.pet_spr.service;

import org.com.pet_spr.domain.dto.response.CartDto;
import org.com.pet_spr.domain.dto.response.CommonResponseDto;

public interface CartService {

    CartDto getCart();

    CommonResponseDto deleteCart();
}
