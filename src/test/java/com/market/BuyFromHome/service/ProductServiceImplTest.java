package com.market.BuyFromHome.service;

import com.market.BuyFromHome.dto.requestDto.productRequest.ProductRequestDto;
import com.market.BuyFromHome.dto.responseDto.productResponse.ProductResponseDto;
import com.market.BuyFromHome.model.Product;
import com.market.BuyFromHome.model.ProductCategory;
import com.market.BuyFromHome.repository.ProductCategoryRepository;
import com.market.BuyFromHome.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductCategoryRepository productCategoryRepository;

    @InjectMocks
    private ProductServiceImpl productServiceImpl;

    @Test
    @DisplayName("Should create product successfully")
    void createProductSuccessfully(){

        ProductRequestDto requestDto = new ProductRequestDto();
        requestDto.setProductName("Rice");
        requestDto.setProductDescription("Long grain foreign rice");
        requestDto.setProductCategoryId(1L);

        ProductCategory category = ProductCategory.builder()
                .id(1L)
                .name("Grain")
                .build();

        when(productCategoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProductResponseDto responseDto =
                productServiceImpl.createProduct(requestDto);

        assertThat(responseDto.getProductName()).isEqualTo("Rice");
        assertThat(responseDto.getProductDescription())
                .isEqualTo("Long grain foreign rice");
        assertThat(responseDto.getProductCategoryId()).isEqualTo(1L);

        verify(productRepository).save(any(Product.class));
    }

}