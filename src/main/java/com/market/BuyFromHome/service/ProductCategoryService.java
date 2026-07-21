package com.market.BuyFromHome.service;

import com.market.BuyFromHome.dto.requestDto.productCategoryRequest.ProductCategoryRequestDto;
import com.market.BuyFromHome.dto.responseDto.productCategoryResponse.ProductCategoryResponseDto;
import jakarta.transaction.Transactional;

import java.util.List;

public interface ProductCategoryService {
    @Transactional
    ProductCategoryResponseDto createProductCategory(
            ProductCategoryRequestDto requestDto);

    @Transactional
    ProductCategoryResponseDto getProductCategoryById(Long id);

    @Transactional
    List<ProductCategoryResponseDto> getAllProductCategories();


    @Transactional
    ProductCategoryResponseDto updateProductCategory(
            Long id,
            ProductCategoryRequestDto requestDto);

    @Transactional
    ProductCategoryResponseDto disableProductCategory(Long id);

    @Transactional
    ProductCategoryResponseDto enableProductCategory(Long id);
}
