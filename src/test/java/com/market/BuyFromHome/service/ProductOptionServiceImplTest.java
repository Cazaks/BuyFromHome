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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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
    @DisplayName("Should throw exception when product does not exist")
    void shouldThrowExceptionWhenProductDoesNotExist() {

        ProductOptionRequestDto requestDto =
                buildRequestDto(
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
    @DisplayName("Should throw exception when product option already exists")
    void shouldThrowExceptionWhenProductOptionAlreadyExists() {

        Product product = buildProduct(1L, "Rice");

        ProductOptionRequestDto requestDto =
                buildRequestDto(
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

    @Test
    @DisplayName("Should trim product specification before saving")
    void shouldTrimProductSpecificationBeforeSaving() {

        Product product = buildProduct(1L, "Rice");

        ProductOptionRequestDto requestDto =
                buildRequestDto(
                        "Local Rice",
                        "   Short Grain   "
                );

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(productOptionRepository
                .existsByProduct_ProductIdAndProductVarietyIgnoreCaseAndProductSpecificationIgnoreCase(
                        1L,
                        "Local Rice",
                        "Short Grain"
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

        assertThat(response.getProductSpecification())
                .isEqualTo("Short Grain");

        verify(productOptionRepository)
                .existsByProduct_ProductIdAndProductVarietyIgnoreCaseAndProductSpecificationIgnoreCase(
                        1L,
                        "Local Rice",
                        "Short Grain"
                );

        verify(productOptionRepository)
                .save(any(ProductOption.class));
    }

    @Test
    @DisplayName("Should get product option by id")
    void shouldGetProductOptionById() {

        Product product = buildProduct(1L, "Rice");

        ProductOption productOption = ProductOption.builder()
                .productOptionId(1L)
                .product(product)
                .productVariety("Local Rice")
                .productSpecification("Short Grain")
                .enabled(true)
                .build();

        when(productOptionRepository.findById(1L))
                .thenReturn(Optional.of(productOption));

        ProductOptionResponseDto response =
                productOptionServiceImpl.getProductOptionById(1L);

        assertThat(response).isNotNull();
        assertThat(response.getProductOptionId()).isEqualTo(1L);
        assertThat(response.getProductId()).isEqualTo(1L);
        assertThat(response.getProductName()).isEqualTo("Rice");
        assertThat(response.getProductVariety()).isEqualTo("Local Rice");
        assertThat(response.getProductSpecification()).isEqualTo("Short Grain");
        assertThat(response.isEnabled()).isTrue();

        verify(productOptionRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when product option does not exist")
    void shouldThrowExceptionWhenProductOptionDoesNotExist() {

        when(productOptionRepository.findById(1L))
                .thenReturn(Optional.empty());

        AppException exception = assertThrows(
                AppException.class,
                () -> productOptionServiceImpl.getProductOptionById(1L)
        );

        assertThat(exception.getMessage())
                .isEqualTo("Product option not found.");

        assertThat(exception.getStatus())
                .isEqualTo(HttpStatus.NOT_FOUND);

        verify(productOptionRepository).findById(1L);
    }


    @Test
    @DisplayName("Should get all product options")
    void shouldGetAllProductOptions() {

        Product rice = buildProduct(1L, "Rice");
        Product beans = buildProduct(2L, "Beans");

        ProductOption riceOption = ProductOption.builder()
                .productOptionId(1L)
                .product(rice)
                .productVariety("Local Rice")
                .productSpecification("Short Grain")
                .enabled(true)
                .build();

        ProductOption beansOption = ProductOption.builder()
                .productOptionId(2L)
                .product(beans)
                .productVariety("Honey Beans")
                .productSpecification("Large Seed")
                .enabled(true)
                .build();

        when(productOptionRepository.findAll())
                .thenReturn(List.of(riceOption, beansOption));

        List<ProductOptionResponseDto> response =
                productOptionServiceImpl.getAllProductOptions();

        assertThat(response).hasSize(2);

        assertThat(response.getFirst().getProductName())
                .isEqualTo("Rice");

        assertThat(response.getFirst().getProductVariety())
                .isEqualTo("Local Rice");

        assertThat(response.get(1).getProductName())
                .isEqualTo("Beans");

        assertThat(response.get(1).getProductVariety())
                .isEqualTo("Honey Beans");

        verify(productOptionRepository).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no product options exist")
    void shouldReturnEmptyListWhenNoProductOptionsExist() {

        when(productOptionRepository.findAll())
                .thenReturn(Collections.emptyList());

        List<ProductOptionResponseDto> response =
                productOptionServiceImpl.getAllProductOptions();

        assertThat(response).isNotNull();
        assertThat(response).isEmpty();

        verify(productOptionRepository).findAll();
    }


    @Test
    @DisplayName("Should get product options by product")
    void shouldGetProductOptionsByProduct() {

        Product product = buildProduct(1L, "Rice");

        ProductOption option1 = ProductOption.builder()
                .productOptionId(1L)
                .product(product)
                .productVariety("Local Rice")
                .productSpecification("Short Grain")
                .enabled(true)
                .build();

        ProductOption option2 = ProductOption.builder()
                .productOptionId(2L)
                .product(product)
                .productVariety("Foreign Rice")
                .productSpecification("Long Grain")
                .enabled(true)
                .build();

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(productOptionRepository.findByProduct_ProductIdAndEnabledTrue(1L))
                .thenReturn(List.of(option1, option2));

        List<ProductOptionResponseDto> response =
                productOptionServiceImpl.getProductOptionsByProduct(1L);

        assertThat(response).hasSize(2);

        assertThat(response.getFirst().getProductOptionId()).isEqualTo(1L);
        assertThat(response.getFirst().getProductName()).isEqualTo("Rice");
        assertThat(response.get(0).getProductVariety()).isEqualTo("Local Rice");
        assertThat(response.get(0).getProductSpecification()).isEqualTo("Short Grain");

        assertThat(response.get(1).getProductOptionId()).isEqualTo(2L);
        assertThat(response.get(1).getProductName()).isEqualTo("Rice");
        assertThat(response.get(1).getProductVariety()).isEqualTo("Foreign Rice");
        assertThat(response.get(1).getProductSpecification()).isEqualTo("Long Grain");

        verify(productRepository).findById(1L);
        verify(productOptionRepository).findByProduct_ProductIdAndEnabledTrue(1L);
    }
    @Test
    @DisplayName("Should return empty list when product has no product options")
    void shouldReturnEmptyListWhenProductHasNoProductOptions() {

        Product product = buildProduct(1L, "Rice");

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(productOptionRepository.findByProduct_ProductIdAndEnabledTrue(1L))
                .thenReturn(Collections.emptyList());

        List<ProductOptionResponseDto> response =
                productOptionServiceImpl.getProductOptionsByProduct(1L);

        assertThat(response).isNotNull();
        assertThat(response).isEmpty();

        verify(productRepository).findById(1L);
        verify(productOptionRepository)
                .findByProduct_ProductIdAndEnabledTrue(1L);
    }

    @Test
    @DisplayName("Should throw exception when product does not exist")
    void shouldThrowExceptionWhenGettingProductOptionsForNonExistingProduct() {

        when(productRepository.findById(1L))
                .thenReturn(Optional.empty());

        AppException exception = assertThrows(
                AppException.class,
                () -> productOptionServiceImpl.getProductOptionsByProduct(1L)
        );

        assertThat(exception.getMessage())
                .isEqualTo("Product not found.");

        assertThat(exception.getStatus())
                .isEqualTo(HttpStatus.NOT_FOUND);

        verify(productRepository).findById(1L);

        verify(productOptionRepository, never())
                .findByProduct_ProductIdAndEnabledTrue(anyLong());
    }
    @Test
    @DisplayName("Should update product option successfully")
    void shouldUpdateProductOptionSuccessfully() {

        Product product = buildProduct(1L, "Rice");

        ProductOption existingOption = ProductOption.builder()
                .productOptionId(1L)
                .product(product)
                .productVariety("Local Rice")
                .productSpecification("Short Grain")
                .enabled(true)
                .build();

        ProductOptionRequestDto requestDto =
                buildRequestDto(
                        "Foreign Rice",
                        "Long Grain"
                );

        when(productOptionRepository.findById(1L))
                .thenReturn(Optional.of(existingOption));

        when(productOptionRepository
                .existsByProduct_ProductIdAndProductVarietyIgnoreCaseAndProductSpecificationIgnoreCaseAndProductOptionIdNot(
                        1L,
                        "Foreign Rice",
                        "Long Grain",
                        1L
                ))
                .thenReturn(false);

        when(productOptionRepository.save(any(ProductOption.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProductOptionResponseDto response =
                productOptionServiceImpl.updateProductOption(1L, requestDto);

        assertThat(response).isNotNull();
        assertThat(response.getProductOptionId()).isEqualTo(1L);
        assertThat(response.getProductVariety()).isEqualTo("Foreign Rice");
        assertThat(response.getProductSpecification()).isEqualTo("Long Grain");

        verify(productOptionRepository).findById(1L);

        verify(productOptionRepository)
                .existsByProduct_ProductIdAndProductVarietyIgnoreCaseAndProductSpecificationIgnoreCaseAndProductOptionIdNot(
                        1L,
                        "Foreign Rice",
                        "Long Grain",
                        1L
                );

        verify(productOptionRepository).save(existingOption);
    }

    @Test
    @DisplayName("Should throw exception when product option does not exist")
    void shouldThrowExceptionWhenUpdatingNonExistingProductOption() {

        ProductOptionRequestDto requestDto =
                buildRequestDto(
                        "Foreign Rice",
                        "Long Grain"
                );

        when(productOptionRepository.findById(1L))
                .thenReturn(Optional.empty());

        AppException exception = assertThrows(
                AppException.class,
                () -> productOptionServiceImpl.updateProductOption(1L, requestDto)
        );

        assertThat(exception.getMessage())
                .isEqualTo("Product option not found.");

        assertThat(exception.getStatus())
                .isEqualTo(HttpStatus.NOT_FOUND);

        verify(productOptionRepository).findById(1L);

        verify(productOptionRepository, never())
                .save(any(ProductOption.class));
    }

    private Product buildProduct(Long productId, String productName) {

        return Product.builder()
                .productId(productId)
                .productName(productName)
                .build();
    }

    @Test
    @DisplayName("Should throw when updated product option already exists")
    void shouldThrowWhenUpdatedProductOptionAlreadyExists() {

        Product product = buildProduct(1L, "Rice");

        ProductOption existingOption = ProductOption.builder()
                .productOptionId(1L)
                .product(product)
                .productVariety("Local Rice")
                .productSpecification("Short Grain")
                .enabled(true)
                .build();

        ProductOptionRequestDto requestDto =
                buildRequestDto(
                        "Foreign Rice",
                        "Long Grain"
                );

        when(productOptionRepository.findById(1L))
                .thenReturn(Optional.of(existingOption));

        when(productOptionRepository
                .existsByProduct_ProductIdAndProductVarietyIgnoreCaseAndProductSpecificationIgnoreCaseAndProductOptionIdNot(
                        1L,
                        "Foreign Rice",
                        "Long Grain",
                        1L
                ))
                .thenReturn(true);

        AppException exception = assertThrows(
                AppException.class,
                () -> productOptionServiceImpl.updateProductOption(1L, requestDto)
        );

        assertThat(exception.getMessage())
                .isEqualTo("Product option already exists.");

        assertThat(exception.getStatus())
                .isEqualTo(HttpStatus.BAD_REQUEST);

        verify(productOptionRepository).findById(1L);

        verify(productOptionRepository)
                .existsByProduct_ProductIdAndProductVarietyIgnoreCaseAndProductSpecificationIgnoreCaseAndProductOptionIdNot(
                        1L,
                        "Foreign Rice",
                        "Long Grain",
                        1L
                );

        verify(productOptionRepository, never())
                .save(any(ProductOption.class));
    }
    @Test
    @DisplayName("Should update product option successfully when specification is null")
    void shouldUpdateProductOptionSuccessfullyWhenSpecificationIsNull() {

        Product product = buildProduct(1L, "Garri");

        ProductOption existingOption = ProductOption.builder()
                .productOptionId(1L)
                .product(product)
                .productVariety("Yellow Garri")
                .productSpecification("")
                .enabled(true)
                .build();

        ProductOptionRequestDto requestDto =
                buildRequestDto(
                        "White Garri",
                        null
                );

        when(productOptionRepository.findById(1L))
                .thenReturn(Optional.of(existingOption));

        when(productOptionRepository
                .existsByProduct_ProductIdAndProductVarietyIgnoreCaseAndProductSpecificationIgnoreCaseAndProductOptionIdNot(
                        1L,
                        "White Garri",
                        "",
                        1L
                ))
                .thenReturn(false);

        when(productOptionRepository.save(any(ProductOption.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProductOptionResponseDto response =
                productOptionServiceImpl.updateProductOption(1L, requestDto);

        assertThat(response.getProductVariety())
                .isEqualTo("White Garri");

        assertThat(response.getProductSpecification())
                .isEqualTo("");

        verify(productOptionRepository).findById(1L);

        verify(productOptionRepository)
                .existsByProduct_ProductIdAndProductVarietyIgnoreCaseAndProductSpecificationIgnoreCaseAndProductOptionIdNot(
                        1L,
                        "White Garri",
                        "",
                        1L
                );

        verify(productOptionRepository).save(existingOption);
    }

    @Test
    @DisplayName("Should disable product option successfully")
    void shouldDisableProductOptionSuccessfully() {

        Product product = buildProduct(1L, "Rice");

        ProductOption productOption = ProductOption.builder()
                .productOptionId(1L)
                .product(product)
                .productVariety("Local Rice")
                .productSpecification("Short Grain")
                .enabled(true)
                .build();

        when(productOptionRepository.findById(1L))
                .thenReturn(Optional.of(productOption));

        when(productOptionRepository.save(any(ProductOption.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProductOptionResponseDto response =
                productOptionServiceImpl.disableProductOption(1L);

        assertThat(productOption.isEnabled()).isFalse();
        assertThat(response.isEnabled()).isFalse();

        verify(productOptionRepository).findById(1L);
        verify(productOptionRepository).save(productOption);
    }


    @Test
    @DisplayName("Should throw exception when disabling non-existing product option")
    void shouldThrowExceptionWhenDisablingNonExistingProductOption() {

        when(productOptionRepository.findById(1L))
                .thenReturn(Optional.empty());

        AppException exception = assertThrows(
                AppException.class,
                () -> productOptionServiceImpl.disableProductOption(1L)
        );

        assertThat(exception.getMessage())
                .isEqualTo("Product option not found.");

        assertThat(exception.getStatus())
                .isEqualTo(HttpStatus.NOT_FOUND);

        verify(productOptionRepository).findById(1L);
        verify(productOptionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should enable product option successfully")
    void shouldEnableProductOptionSuccessfully() {

        Product product = buildProduct(1L, "Rice");

        ProductOption productOption = ProductOption.builder()
                .productOptionId(1L)
                .product(product)
                .productVariety("Local Rice")
                .productSpecification("Short Grain")
                .enabled(false)
                .build();

        when(productOptionRepository.findById(1L))
                .thenReturn(Optional.of(productOption));

        when(productOptionRepository.save(any(ProductOption.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProductOptionResponseDto response =
                productOptionServiceImpl.enableProductOption(1L);

        assertThat(productOption.isEnabled()).isTrue();
        assertThat(response.isEnabled()).isTrue();

        verify(productOptionRepository).findById(1L);
        verify(productOptionRepository).save(productOption);
    }

    @Test
    @DisplayName("Should throw exception when enabling non-existing product option")
    void shouldThrowExceptionWhenEnablingNonExistingProductOption() {

        when(productOptionRepository.findById(1L))
                .thenReturn(Optional.empty());

        AppException exception = assertThrows(
                AppException.class,
                () -> productOptionServiceImpl.enableProductOption(1L)
        );

        assertThat(exception.getMessage())
                .isEqualTo("Product option not found.");

        assertThat(exception.getStatus())
                .isEqualTo(HttpStatus.NOT_FOUND);

        verify(productOptionRepository).findById(1L);
        verify(productOptionRepository, never()).save(any());
    }

    private ProductOptionRequestDto buildRequestDto(
            String productVariety,
            String productSpecification) {

        ProductOptionRequestDto dto = new ProductOptionRequestDto();

        dto.setProductId(1L);
        dto.setProductVariety(productVariety);
        dto.setProductSpecification(productSpecification);

        return dto;
    }
}