package com.market.BuyFromHome.service;

import com.market.BuyFromHome.dto.requestDto.productRequest.ProductRequestDto;
import com.market.BuyFromHome.dto.responseDto.productResponse.ProductResponseDto;
import jakarta.transaction.Transactional;

import java.util.List;

public interface ProductService {
    @Transactional
    ProductResponseDto createProduct(ProductRequestDto requestDto);

    @Transactional
    ProductResponseDto getProductById(Long productId);

    @Transactional
    List<ProductResponseDto> getAllProducts();

    @Transactional
    ProductResponseDto updateProduct(Long productId, ProductRequestDto requestDto);

    @Transactional
    ProductResponseDto disableProduct(Long productId);

    @Transactional
    ProductResponseDto enableProduct(Long productId);
}
