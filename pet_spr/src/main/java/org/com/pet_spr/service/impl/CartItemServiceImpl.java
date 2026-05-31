package org.com.pet_spr.service.impl;

import jakarta.transaction.Transactional;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.com.pet_spr.constant.ErrorMessage;
import org.com.pet_spr.domain.dto.request.ReqAddCartItem;
import org.com.pet_spr.domain.dto.request.ReqUpdateCartItem;
import org.com.pet_spr.domain.dto.response.CartItemDto;
import org.com.pet_spr.domain.dto.response.CommonResponseDto;
import org.com.pet_spr.domain.entity.*;
import org.com.pet_spr.domain.mapper.CartItemMapper;
import org.com.pet_spr.exception.BadRequestException;
import org.com.pet_spr.exception.ForbiddenException;
import org.com.pet_spr.exception.NotFoundException;
import org.com.pet_spr.exception.UnauthorizedException;
import org.com.pet_spr.repository.*;
import org.com.pet_spr.security.SecurityUtil;
import org.com.pet_spr.service.CartItemService;
import org.com.pet_spr.service.UserService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartItemServiceImpl implements CartItemService {
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserService userService;

    private final CartRepository cartRepository;
    private final InventoryRepository inventoryRepository;

    private final CartItemMapper cartItemMapper;

    @Override
    @Transactional
    public CartItemDto addCartItem(ReqAddCartItem reqAddCartItem) {
        log.info("[CART] Thêm sản phẩm ID: {} vào giỏ hàng", reqAddCartItem.getProductId());


        User currentUser = userService.getUserLogin();

        Cart cart = cartRepository.findByUserId(currentUser.getId()).orElseGet(() -> {
            log.info("[CART] User ID: {} chưa có giỏ hàng, tạo mới", currentUser.getId());
           Cart newCart = new Cart();
           newCart.setUser(currentUser);
           return cartRepository.save(newCart);
        });

        // kiểm tra product có tồn tại không
        Product product = productRepository.findById(reqAddCartItem.getProductId()).orElseThrow(
                () -> new NotFoundException(ErrorMessage.Product.ERR_NOT_FOUND_ID, new String[]{String.valueOf(reqAddCartItem.getProductId())})
        );

        // kiểm tra tồn kho
        Inventory inventory = inventoryRepository.findByProductId(reqAddCartItem.getProductId()).orElseThrow(
                () -> new NotFoundException(ErrorMessage.Inventory.ERR_NOT_FOUND_ID, new String[]{String.valueOf(reqAddCartItem.getProductId())})
        );

        if(inventory.getQuantity() < reqAddCartItem.getQuantity()){
            throw new BadRequestException(ErrorMessage.Inventory.ERR_NOT_ENOUGH_QUANTITY, new String[]{String.valueOf(inventory.getQuantity())});
        }

        // tìm cartItem đã tồn tại chưa
        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId()).orElse(null);
        if(cartItem != null){
            Integer newQty = cartItem.getQuantity() + reqAddCartItem.getQuantity();

            // kiểm tra tồn kho sau khi cộng dồn
            if(inventory.getQuantity() < newQty){
                throw new BadRequestException(ErrorMessage.Inventory.ERR_NOT_ENOUGH_QUANTITY, new String[]{String.valueOf(inventory.getQuantity())});
            }
            log.info("[CART] Cộng dồn quantity {} → {}", cartItem.getQuantity(), newQty);
            cartItem.setQuantity(newQty);
        } else{
            log.info("[CART] Tạo mới CartItem cho Product ID: {}", reqAddCartItem.getProductId());
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(reqAddCartItem.getQuantity());
        }

        cartItemRepository.save(cartItem);
        log.info("[CART] Lưu CartItem thành công | Product ID: {} | Quantity: {}",
                reqAddCartItem.getProductId(), cartItem.getQuantity());

        CartItemDto dto = cartItemMapper.toDto(cartItem);
        return dto;
    }

    @Override
    @Transactional
    public CartItemDto updateCartItem(ReqUpdateCartItem req) {
        log.info("[CART] Cập nhật CartItem ID: {} | Quantity mới: {}", req.getItemId(), req.getQuantity());

        CartItem cartItem = cartItemRepository.findById(req.getItemId()).orElseThrow(
                () -> new NotFoundException(ErrorMessage.CartItem.ERR_NOT_FOUND_ID, new String[]{String.valueOf(req.getItemId())})
        );

        User currentUser = userService.getUserLogin();
        if(!cartItem.getCart().getUser().getId().equals(currentUser.getId()) ){
            throw new ForbiddenException(ErrorMessage.FORBIDDEN);
        }

        if(req.getQuantity() == 0){
            cartItemRepository.delete(cartItem);
            return null;
        }
        Inventory inventory = inventoryRepository.findByProductId(cartItem.getProduct().getId()).orElseThrow(
                () -> new NotFoundException(ErrorMessage.Inventory.ERR_NOT_FOUND_ID, new String[]{String.valueOf(cartItem.getProduct().getId())})
        );
        if(inventory.getQuantity() < req.getQuantity()){
            throw new BadRequestException(ErrorMessage.Inventory.ERR_NOT_ENOUGH_QUANTITY, new String[]{String.valueOf(inventory.getQuantity())});
        }
        cartItem.setQuantity(req.getQuantity());
        cartItemRepository.save(cartItem);
        log.info("[CART] Cập nhật thành công CartItem ID: {}", req.getItemId());
        return cartItemMapper.toDto(cartItem);


    }

    @Override
    @Transactional
    public CommonResponseDto deleteCartItem(Long itemId) {
        log.info("[CART] Xóa CartItem ID: {}", itemId);
        CartItem cartItem = cartItemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException(ErrorMessage.CartItem.ERR_NOT_FOUND_ID, new String[]{String.valueOf(itemId)})
        );
        User currentUser = userService.getUserLogin();
        if(!cartItem.getCart().getUser().getId().equals(currentUser.getId())){
            throw new ForbiddenException(ErrorMessage.FORBIDDEN);
        }
        cartItemRepository.delete(cartItem);
        log.info("[CART] Xóa thành công CartItem ID: {}", itemId);
        return new CommonResponseDto(true, "Delete cart item successfully");
    }
}
