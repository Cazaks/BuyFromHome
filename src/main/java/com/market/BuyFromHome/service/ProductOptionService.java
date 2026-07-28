package com.market.BuyFromHome.service;

import com.market.BuyFromHome.dto.requestDto.productOptionRequest.ProductOptionRequestDto;
import com.market.BuyFromHome.dto.responseDto.productOptionResponse.ProductOptionResponseDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ProductOptionService {
    ProductOptionResponseDto createProductOption(ProductOptionRequestDto requestDto);

    ProductOptionResponseDto getProductOptionById(Long productOptionId);

    @Transactional(readOnly = true)
    List<ProductOptionResponseDto> getAllProductOptions();

    @Transactional(readOnly = true)
    List<ProductOptionResponseDto> getProductOptionsByProduct(Long productId);

    @Transactional
    ProductOptionResponseDto updateProductOption(
            Long productOptionId,
            ProductOptionRequestDto requestDto);

    @Transactional
    void disableProductOption(Long productOptionId);

    @Transactional
    void enableProductOption(Long productOptionId);
}
