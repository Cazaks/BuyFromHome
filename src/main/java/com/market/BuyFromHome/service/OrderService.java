package com.market.BuyFromHome.service;

import com.market.BuyFromHome.dto.requestDto.orderRequest.OrderRequestDto;
import com.market.BuyFromHome.dto.responseDto.orderResponse.OrderResponseDto;
import org.springframework.transaction.annotation.Transactional;

public interface OrderService {
    @Transactional
    OrderResponseDto createOrder(Long userId, OrderRequestDto requestDto);
}
