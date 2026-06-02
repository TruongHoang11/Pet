package org.com.pet_spr.service;

import org.com.pet_spr.constant.OrderStatus;
import org.com.pet_spr.domain.dto.pagination.ResultPaginationDto;
import org.com.pet_spr.domain.dto.request.ReqCreateOrderBuyNow;
import org.com.pet_spr.domain.dto.request.ReqCreateOrderFromCart;
import org.com.pet_spr.domain.dto.request.ReqUpdateOrderStatus;
import org.com.pet_spr.domain.dto.response.OrderDto;
import org.com.pet_spr.domain.dto.response.OrderStatusHistoryDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {
    OrderDto createOrderFromCart(ReqCreateOrderFromCart req);

    OrderDto createOrderFromBuyNow(ReqCreateOrderBuyNow req);

    ResultPaginationDto getMyOrders(OrderStatus orderStatus, Pageable pageable);

    OrderDto getOrderDetail(Long orderId);

    OrderDto cancelOrder(Long orderId);

    OrderDto updateOrderStatus(ReqUpdateOrderStatus req);

    ResultPaginationDto getAllOrders(List<String> filter, Pageable pageable);

    OrderDto getOrderDetailAdmin(Long orderId);

    List<OrderStatusHistoryDto> getOrderStatusHistory(Long orderId);
}