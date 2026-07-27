package com.market.BuyFromHome.service;

import com.market.BuyFromHome.dto.requestDto.productOptionRequest.ProductOptionRequestDto;
import com.market.BuyFromHome.dto.responseDto.productOptionResponse.ProductOptionResponseDto;
import com.market.BuyFromHome.exception.AppException;
import com.market.BuyFromHome.model.Product;
import com.market.BuyFromHome.model.ProductOption;
import com.market.BuyFromHome.repository.ProductOptionRepository;
import com.market.BuyFromHome.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductOptionServiceImplTest {

    @Mock
    private ProductOptionRepository productOptionRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductOptionServiceImpl productOptionServiceImpl;

    @Test
    @DisplayName("Should create product option successfully")
    void shouldCreateProductOptionSuccessfully() {

        Product product = buildProduct(1L, "Rice");

        ProductOptionRequestDto requestDto =
                buildRequestDto(
                        1L,
                        "Local Rice",
                        "Short Grain"
                );

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(productOptionRepository
                .existsByProduct_ProductIdAndProductVarietyIgnoreCaseAndProductSpecificationIgnoreCase(
                        1L,
                        "Local Rice",
                        "Short Grain"))
                .thenReturn(false);

        when(productOptionRepository.save(any(ProductOption.class)))
                .thenAnswer(invocation -> {

                    ProductOption option = invocation.getArgument(0);
                    option.setProductOptionId(1L);

                    return option;
                });

        ProductOptionResponseDto response =
                productOptionServiceImpl.createProductOption(requestDto);

        assertThat(response).isNotNull();
        assertThat(response.getProductOptionId()).isEqualTo(1L);
        assertThat(response.getProductId()).isEqualTo(1L);
        assertThat(response.getProductName()).isEqualTo("Rice");
        assertThat(response.getProductVariety()).isEqualTo("Local Rice");
        assertThat(response.getProductSpecification()).isEqualTo("Short Grain");

        verify(productOptionRepository).save(any(ProductOption.class));
    }

    @Test
    @DisplayName("Should throw when product does not exist")
    void shouldThrowWhenProductDoesNotExist() {

        ProductOptionRequestDto requestDto =
                buildRequestDto(
                        1L,
                        "Local Rice",
                        "Short Grain"
                );

        when(productRepository.findById(1L))
                .thenReturn(Optional.empty());


        AppException exception = assertThrows(
                AppException.class,
                () -> productOptionServiceImpl.createProductOption(requestDto)
        );

        assertThat(exception.getMessage())
                .isEqualTo("Product not found.");

        assertThat(exception.getStatus())
                .isEqualTo(HttpStatus.NOT_FOUND);

        verify(productRepository).findById(1L);

        verify(productOptionRepository, never())
                .existsByProduct_ProductIdAndProductVarietyIgnoreCaseAndProductSpecificationIgnoreCase(
                        anyLong(),
                        anyString(),
                        anyString()
                );

        verify(productOptionRepository, never())
                .save(any(ProductOption.class));
    }

    @Test
    @DisplayName("Should throw when product option already exists")
    void shouldThrowWhenProductOptionAlreadyExists() {

        Product product = buildProduct(1L, "Rice");

        ProductOptionRequestDto requestDto =
                buildRequestDto(
                        1L,
                        "Local Rice",
                        "Short Grain"
                );

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(productOptionRepository
                .existsByProduct_ProductIdAndProductVarietyIgnoreCaseAndProductSpecificationIgnoreCase(
                        1L,
                        "Local Rice",
                        "Short Grain"))
                .thenReturn(true);

        AppException exception = assertThrows(
                AppException.class,
                () -> productOptionServiceImpl.createProductOption(requestDto)
        );

        assertThat(exception.getMessage())
                .isEqualTo("Product option already exists.");

        assertThat(exception.getStatus())
                .isEqualTo(HttpStatus.BAD_REQUEST);

        verify(productRepository).findById(1L);

        verify(productOptionRepository)
                .existsByProduct_ProductIdAndProductVarietyIgnoreCaseAndProductSpecificationIgnoreCase(
                        1L,
                        "Local Rice",
                        "Short Grain"
                );

        verify(productOptionRepository, never())
                .save(any(ProductOption.class));
    }

    @Test
    @DisplayName("Should create product option successfully when specification is null")
    void shouldCreateProductOptionSuccessfullyWhenSpecificationIsNull() {

        Product product = buildProduct(1L, "Garri");

        ProductOptionRequestDto requestDto =
                buildRequestDto(
                        1L,
                        "Yellow Garri",
                        null
                );

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(productOptionRepository
                .existsByProduct_ProductIdAndProductVarietyIgnoreCaseAndProductSpecificationIgnoreCase(
                        1L,
                        "Yellow Garri",
                        ""
                ))
                .thenReturn(false);

        when(productOptionRepository.save(any(ProductOption.class)))
                .thenAnswer(invocation -> {
                    ProductOption option = invocation.getArgument(0);
                    option.setProductOptionId(1L);
                    return option;
                });

        ProductOptionResponseDto response =
                productOptionServiceImpl.createProductOption(requestDto);

        assertThat(response).isNotNull();
        assertThat(response.getProductOptionId()).isEqualTo(1L);
        assertThat(response.getProductId()).isEqualTo(1L);
        assertThat(response.getProductName()).isEqualTo("Garri");
        assertThat(response.getProductVariety()).isEqualTo("Yellow Garri");
        assertThat(response.getProductSpecification()).isEqualTo("");

        verify(productRepository).findById(1L);

        verify(productOptionRepository)
                .existsByProduct_ProductIdAndProductVarietyIgnoreCaseAndProductSpecificationIgnoreCase(
                        1L,
                        "Yellow Garri",
                        ""
                );

        verify(productOptionRepository)
                .save(any(ProductOption.class));
    }


    private Product buildProduct(Long productId, String productName) {

        return Product.builder()
                .productId(productId)
                .productName(productName)
                .build();
    }

    private ProductOptionRequestDto buildRequestDto(
            Long productId,
            String productVariety,
            String productSpecification) {

        ProductOptionRequestDto dto = new ProductOptionRequestDto();

        dto.setProductId(productId);
        dto.setProductVariety(productVariety);
        dto.setProductSpecification(productSpecification);

        return dto;
    }
}