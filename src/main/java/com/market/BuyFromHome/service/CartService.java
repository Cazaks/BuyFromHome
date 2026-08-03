package com.market.BuyFromHome.service;

import com.market.BuyFromHome.dto.requestDto.cartItemRequest.CartItemRequestDto;
import com.market.BuyFromHome.dto.responseDto.cartResponse.CartResponseDto;
import jakarta.transaction.Transactional;

public interface CartService {
    @Transactional
    CartResponseDto getCart(Long userId);

    @Transactional
    CartResponseDto addItem(
            Long userId,
            CartItemRequestDto requestDto);

    @Transactional
    CartResponseDto updateItemQuantity(
            Long userId,
            Long cartItemId,
            int quantity);

    @Transactional
    CartResponseDto removeItem(
            Long userId,
            Long cartItemId);

    @Transactional
    CartResponseDto clearCart(Long userId);
}
