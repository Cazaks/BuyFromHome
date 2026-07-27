package com.market.BuyFromHome.service;

import com.market.BuyFromHome.dto.requestDto.productOptionRequest.ProductOptionRequestDto;
import com.market.BuyFromHome.dto.responseDto.productOptionResponse.ProductOptionResponseDto;

public interface ProductOptionService {
    ProductOptionResponseDto createProductOption(ProductOptionRequestDto requestDto);

    ProductOptionResponseDto getProductOptionById(Long productOptionId);
}
