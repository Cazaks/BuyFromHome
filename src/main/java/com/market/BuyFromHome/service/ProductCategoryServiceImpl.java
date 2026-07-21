package com.market.BuyFromHome.service;

import com.market.BuyFromHome.dto.requestDto.productCategoryRequest.ProductCategoryRequestDto;
import com.market.BuyFromHome.dto.responseDto.productCategoryResponse.ProductCategoryResponseDto;
import com.market.BuyFromHome.model.ProductCategory;
import com.market.BuyFromHome.repository.ProductCategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductCategoryServiceImpl implements ProductCategoryService{

    private final ProductCategoryRepository productCategoryRepository;

    @Transactional
    @Override
    public ProductCategoryResponseDto createProductCategory(
            ProductCategoryRequestDto requestDto) {

        if (productCategoryRepository.existsByNameIgnoreCase(requestDto.getName())) {
            throw new RuntimeException(
                    "Product category already exists: " + requestDto.getName());
        }

        ProductCategory productCategory = ProductCategory.builder()
                .name(requestDto.getName())
                .description(requestDto.getDescription())
                .enabled(true)
                .build();

        ProductCategory savedCategory =
                productCategoryRepository.save(productCategory);

        return ProductCategoryResponseDto.builder()
                .id(savedCategory.getId())
                .name(savedCategory.getName())
                .description(savedCategory.getDescription())
                .enabled(savedCategory.isEnabled())
                .build();
    }

}
