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
}
