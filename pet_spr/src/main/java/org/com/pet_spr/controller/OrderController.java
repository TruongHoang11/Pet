package org.com.pet_spr.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.com.pet_spr.base.RestApiV1;
import org.com.pet_spr.base.VsResponseUtil;
import org.com.pet_spr.constant.OrderStatus;
import org.com.pet_spr.constant.UrlConstant;
import org.com.pet_spr.domain.dto.request.ReqCreateOrderBuyNow;
import org.com.pet_spr.domain.dto.request.ReqCreateOrderFromCart;
import org.com.pet_spr.domain.dto.request.ReqUpdateOrderStatus;
import org.com.pet_spr.domain.dto.response.OrderDto;
import org.com.pet_spr.service.OrderService;
import org.com.pet_spr.service.ProductService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestApiV1
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping(UrlConstant.Order.GET_ORDER_DETAIL_ADMIN)
    public ResponseEntity<?> getOrderDetailAdmin(@PathVariable Long id) {
        return VsResponseUtil.success(HttpStatus.OK, orderService.getOrderDetailAdmin(id));
    }


    @GetMapping(UrlConstant.Order.GET_ALL_ORDERS)
    public ResponseEntity<?> getAllOrders (
            @RequestParam(required = false) List<String> filter,
            Pageable pageable
    ){
        return VsResponseUtil.success(HttpStatus.OK,orderService.getAllOrders(filter, pageable));

    }

    @PostMapping(UrlConstant.Order.CREATE_ORDER_FROM_CART)
    public ResponseEntity<?> createOrderFromCart(@RequestBody @Valid ReqCreateOrderFromCart req)  {
        OrderDto orderDto = orderService.createOrderFromCart(req);

        return VsResponseUtil.success(HttpStatus.OK, orderDto);
    }


    @PostMapping(UrlConstant.Order.CREATE_ORDER_FROM_BUY_NOW)
    public ResponseEntity<?> createOrderFromBuyNow(@RequestBody @Valid ReqCreateOrderBuyNow req)  {
        OrderDto orderDto = orderService.createOrderFromBuyNow(req);

        return VsResponseUtil.success(HttpStatus.OK, orderDto);
    }

    @PatchMapping(UrlConstant.Order.CANCEL_ORDER)
    public ResponseEntity<?> cancelOrder(@PathVariable Long id) {
        return VsResponseUtil.success(HttpStatus.OK, orderService.cancelOrder(id));

    }

    @PatchMapping(UrlConstant.Order.UPDATE_ORDER_STATUS)
    public ResponseEntity<?> updateOrderStatus(@RequestBody ReqUpdateOrderStatus req) {
        return VsResponseUtil.success(HttpStatus.OK, orderService.updateOrderStatus(req));
    }

    @GetMapping(UrlConstant.Order.GET_MY_ORDERS)
    public ResponseEntity<?> getMyOrders(
            @RequestParam(required = false) OrderStatus status,
            Pageable pageable){

        return VsResponseUtil.success(HttpStatus.OK,orderService.getMyOrders(status, pageable) );

    }


}
