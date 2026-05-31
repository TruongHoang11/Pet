package org.com.pet_spr.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.pet_spr.constant.ErrorMessage;
import org.com.pet_spr.domain.dto.response.CartDto;
import org.com.pet_spr.domain.dto.response.CartItemDto;
import org.com.pet_spr.domain.dto.response.CommonResponseDto;
import org.com.pet_spr.domain.entity.Cart;
import org.com.pet_spr.domain.entity.User;
import org.com.pet_spr.domain.mapper.CartItemMapper;
import org.com.pet_spr.exception.NotFoundException;
import org.com.pet_spr.repository.CartRepository;
import org.com.pet_spr.service.CartService;
import org.com.pet_spr.service.UserService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final UserService userService;
    private final CartItemMapper cartItemMapper;

    @Override
    public CartDto getCart() {
        log.info("[CART] Xem giỏ hàng của user hiện tại");
        User currentUser = userService.getUserLogin();
        Cart cart = cartRepository.findByUserId(currentUser.getId()).orElse(null);
        if(cart == null){
            log.info("[CART] Giỏ hàng trống | User ID: {}", currentUser.getId());
            return CartDto.builder()
                    .totalItem(0)
                    .totalAmount(BigDecimal.ZERO)
                    .itemDtoList(Collections.emptyList())
                    .build();

        }

        List<CartItemDto> cartItems = cart.getCartItems().stream()
                .map(cartItem -> {
                    return cartItemMapper.toDto(cartItem);
                }).toList();

        // Tính tổng tiền giỏ hàng bằng cách bóc tách totalPrice của
        // từng item rồi cộng dồn lại (mặc định là 0 nếu giỏ trống)
        BigDecimal totalAmount = cartItems.stream().
                map(CartItemDto::getTotalPrice)
                // .reduce(BigDecimal.ZERO, BigDecimal::add);
                .reduce(BigDecimal.ZERO, (sum, price) -> sum.add(price));
        //BigDecimal.ZERO: Giá trị bắt đầu (gán biến tích lũy ban đầu bằng 0).
        //sum: Biến tích lũy (giống như biến total giữ tổng số tiền hiện tại qua các lượt cộng).
        //price: Giá tiền của món hàng hiện tại đang được duyệt qua trên băng chuyền.
        //sum ban đầu = 0. Gặp món 100k -> lấy 0 + 100k = 100k. Lúc này sum trở thành 100k.


        log.info("[CART] User ID: {} | Số item: {} | Tổng tiền: {}",
                currentUser.getId(), cartItems.size(), totalAmount);
        return CartDto.builder()
                .id(cart.getId())
                .totalAmount(totalAmount)
                .totalItem(cartItems.size())
                .itemDtoList(cartItems)
                .build();
    }

    @Override
    public CommonResponseDto deleteCart() {
        log.info("[CART] Xóa toàn bộ giỏ hàng");

        //lay user hien tai
        User currentUser = userService.getUserLogin();

        // tim card
        Cart cart = cartRepository.findByUserId(currentUser.getId()).orElseThrow(
                () -> new NotFoundException(ErrorMessage.Cart.ERR_CARD_NOT_FOUND)
        );

        //xoa toan bo card item
        cart.getCartItems().clear(); // nhờ CascadeType.ALL + orphanRemoval = true
        cartRepository.save(cart);
        log.info("[CART] Xóa toàn bộ giỏ hàng thành công | User ID: {}", currentUser.getId());
        return new CommonResponseDto(true, "Xóa giỏ hàng thành công");
    }
}
