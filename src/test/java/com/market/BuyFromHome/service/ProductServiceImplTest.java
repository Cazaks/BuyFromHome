package com.market.BuyFromHome.service;

import com.market.BuyFromHome.dto.requestDto.productRequest.ProductRequestDto;
import com.market.BuyFromHome.dto.responseDto.productResponse.ProductResponseDto;
import com.market.BuyFromHome.exception.AppException;
import com.market.BuyFromHome.model.Product;
import com.market.BuyFromHome.model.ProductCategory;
import com.market.BuyFromHome.repository.ProductCategoryRepository;
import com.market.BuyFromHome.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.Optional;

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

    @Test
    @DisplayName("Should throw exception when product already exists")
    void throwProductAlreadyExists() {

        ProductRequestDto requestDto = new ProductRequestDto();

        requestDto.setProductName("Rice");
        requestDto.setProductDescription("50kg Bag of Rice");
        requestDto.setProductCategoryId(1L);

        when(productRepository.existsByProductNameIgnoreCase("Rice"))
                .thenReturn(true);

        assertThatThrownBy(() ->
                productServiceImpl.createProduct(requestDto))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("Product already exists")
                .extracting(ex -> ((AppException) ex).getStatus())
                .isEqualTo(HttpStatus.CONFLICT);

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw exception when product category does not exist")
    void throwProductCategoryNotFound() {

        ProductRequestDto requestDto = new ProductRequestDto();

        requestDto.setProductName("Rice");
        requestDto.setProductDescription("50kg Bag of Rice");
        requestDto.setProductCategoryId(1L);

        when(productRepository.existsByProductNameIgnoreCase("Rice"))
                .thenReturn(false);

        when(productCategoryRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                productServiceImpl.createProduct(requestDto))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("Product category not found")
                .extracting(ex -> ((AppException) ex).getStatus())
                .isEqualTo(HttpStatus.NOT_FOUND);

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Should save product with correct details")
    void shouldSaveProductWithCorrectDetails() {

        ProductRequestDto requestDto = new ProductRequestDto();
        requestDto.setProductName("Rice");
        requestDto.setProductDescription("50kg Bag of Rice");
        requestDto.setProductCategoryId(1L);

        ProductCategory category = ProductCategory.builder()
                .id(1L)
                .name("Grains")
                .build();

        when(productRepository.existsByProductNameIgnoreCase("Rice"))
                .thenReturn(false);

        when(productCategoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        productServiceImpl.createProduct(requestDto);

        ArgumentCaptor<Product> productCaptor =
                ArgumentCaptor.forClass(Product.class);

        verify(productRepository).save(productCaptor.capture());

        Product savedProduct = productCaptor.getValue();

        assertThat(savedProduct.getProductName()).isEqualTo("Rice");
        assertThat(savedProduct.getProductDescription())
                .isEqualTo("50kg Bag of Rice");
        assertThat(savedProduct.getCategory()).isEqualTo(category);
        assertThat(savedProduct.isEnabled()).isTrue();
    }

}