package com.market.BuyFromHome.service;

import com.market.BuyFromHome.dto.requestDto.productCategoryRequest.ProductCategoryRequestDto;
import com.market.BuyFromHome.dto.responseDto.productCategoryResponse.ProductCategoryResponseDto;
import com.market.BuyFromHome.model.ProductCategory;
import com.market.BuyFromHome.repository.ProductCategoryRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductCategoryServiceImplTest {

    @Mock
    ProductCategoryRepository productCategoryRepository;

    @InjectMocks
    ProductCategoryServiceImpl productCategoryServiceImpl;

    @Test
    @DisplayName("Should create product category successfully")
    void createProductCategorySuccessfully() {

        ProductCategoryRequestDto requestDto = new ProductCategoryRequestDto();
        requestDto.setName("Grains");
        requestDto.setDescription("Rice, Beans, Garri");

        when(productCategoryRepository.existsByNameIgnoreCase("Grains"))
                .thenReturn(false);

        when(productCategoryRepository.save(any(ProductCategory.class)))
                .thenAnswer(i -> i.getArgument(0));

        ProductCategoryResponseDto responseDto =
                productCategoryServiceImpl.createProductCategory(requestDto);

        assertThat(responseDto.getName()).isEqualTo("Grains");
        assertThat(responseDto.getDescription())
                .isEqualTo("Rice, Beans, Garri");

        verify(productCategoryRepository)
                .save(any(ProductCategory.class));
    }

}