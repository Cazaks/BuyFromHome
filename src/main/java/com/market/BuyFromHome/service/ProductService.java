package com.market.BuyFromHome.service;

import com.market.BuyFromHome.dto.requestDto.productRequest.ProductRequestDto;
import com.market.BuyFromHome.dto.responseDto.productResponse.ProductResponseDto;
import jakarta.transaction.Transactional;

public interface ProductService {
    @Transactional
    ProductResponseDto createProduct(ProductRequestDto requestDto);

    @Transactional
    ProductResponseDto getProductById(Long productId);
}
