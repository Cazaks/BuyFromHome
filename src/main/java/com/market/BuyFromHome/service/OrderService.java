package com.market.BuyFromHome.service;

import com.market.BuyFromHome.dto.requestDto.orderRequest.OrderRequestDto;
import com.market.BuyFromHome.dto.responseDto.orderResponse.OrderResponseDto;
import com.market.BuyFromHome.enums.OrderStatus;
import com.market.BuyFromHome.enums.PaymentStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OrderService {
    @Transactional
    OrderResponseDto createOrder(Long userId, OrderRequestDto requestDto);

    @Transactional
    OrderResponseDto getOrderById(Long userId, Long orderId);

    @Transactional
    List<OrderResponseDto> getOrdersForUser(Long userId);

    @Transactional
    List<OrderResponseDto> getAllOrders();

    @Transactional
    OrderResponseDto getOrderByIdAdmin(Long orderId);

    @Transactional
    OrderResponseDto updateOrderStatus(Long orderId, OrderStatus status);

    @Transactional
    OrderResponseDto updatePaymentStatus(Long orderId, PaymentStatus paymentStatus);
}
