package com.market.BuyFromHome.service;

import com.market.BuyFromHome.dto.responseDto.cartResponse.CartResponseDto;
import jakarta.transaction.Transactional;

public interface CartService {
    @Transactional
    CartResponseDto getCart(Long userId);
}
