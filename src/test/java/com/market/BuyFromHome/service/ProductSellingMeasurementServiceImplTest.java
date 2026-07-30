package com.market.BuyFromHome.service;

import com.market.BuyFromHome.dto.requestDto.productSellingRequest.ProductSellingMeasurementRequestDto;
import com.market.BuyFromHome.dto.responseDto.productSellingResponse.ProductSellingMeasurementResponseDto;
import com.market.BuyFromHome.enums.MeasurementUnit;
import com.market.BuyFromHome.exception.AppException;
import com.market.BuyFromHome.model.Product;
import com.market.BuyFromHome.model.ProductOption;
import com.market.BuyFromHome.model.ProductSellingMeasurement;
import com.market.BuyFromHome.repository.ProductOptionRepository;
import com.market.BuyFromHome.repository.ProductSellingMeasurementRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
class ProductSellingMeasurementServiceImplTest {

    @Mock
    private ProductSellingMeasurementRepository productSellingMeasurementRepository;

    @Mock
    private ProductOptionRepository productOptionRepository;

    @InjectMocks
    private ProductSellingMeasurementServiceImpl productSellingMeasurementServiceImpl;

    @Test
    @DisplayName("Should create selling measurement successfully")
    void shouldCreateSellingMeasurementSuccessfully() {

        ProductOption productOption = buildProductOption();

        ProductSellingMeasurementRequestDto requestDto =
                buildRequestDto();

        when(productOptionRepository.findById(1L))
                .thenReturn(Optional.of(productOption));

        when(productSellingMeasurementRepository
                .existsByProductOption_ProductOptionIdAndMeasurementUnit(
                        1L,
                        MeasurementUnit.DERICA
                ))
                .thenReturn(false);

        when(productSellingMeasurementRepository.save(
                any(ProductSellingMeasurement.class)))
                .thenAnswer(invocation -> {

                    ProductSellingMeasurement measurement =
                            invocation.getArgument(0);

                    measurement.setSellingMeasurementId(1L);

                    return measurement;
                });

        ProductSellingMeasurementResponseDto response =
                productSellingMeasurementServiceImpl
                        .createSellingMeasurement(requestDto);

        assertThat(response).isNotNull();
        assertThat(response.getSellingMeasurementId())
                .isEqualTo(1L);
        assertThat(response.getProductOptionId())
                .isEqualTo(1L);
        assertThat(response.getMeasurementUnit())
                .isEqualTo(MeasurementUnit.DERICA);
        assertThat(response.getSellingPrice())
                .isEqualByComparingTo("2500.00");
        assertThat(response.getQuantityInStock())
                .isEqualTo(20);

        verify(productSellingMeasurementRepository)
                .save(any(ProductSellingMeasurement.class));
    }


    @Test
    @DisplayName("Should throw exception when product option does not exist")
    void shouldThrowExceptionWhenProductOptionDoesNotExist() {

        ProductSellingMeasurementRequestDto requestDto =
                buildRequestDto();

        when(productOptionRepository.findById(1L))
                .thenReturn(Optional.empty());

        AppException exception = assertThrows(
                AppException.class,
                () -> productSellingMeasurementServiceImpl
                        .createSellingMeasurement(requestDto)
        );

        assertThat(exception.getMessage())
                .isEqualTo("Product option not found.");

        assertThat(exception.getStatus())
                .isEqualTo(HttpStatus.NOT_FOUND);

        verify(productOptionRepository).findById(1L);

        verify(productSellingMeasurementRepository, never())
                .existsByProductOption_ProductOptionIdAndMeasurementUnit(
                        anyLong(),
                        any()
                );

        verify(productSellingMeasurementRepository, never())
                .save(any(ProductSellingMeasurement.class));
    }

    @Test
    @DisplayName("Should throw exception when selling measurement already exists")
    void shouldThrowExceptionWhenSellingMeasurementAlreadyExists() {

        ProductOption productOption = buildProductOption();

        ProductSellingMeasurementRequestDto requestDto =
                buildRequestDto();

        when(productOptionRepository.findById(1L))
                .thenReturn(Optional.of(productOption));

        when(productSellingMeasurementRepository
                .existsByProductOption_ProductOptionIdAndMeasurementUnit(
                        1L,
                        MeasurementUnit.DERICA
                ))
                .thenReturn(true);

        AppException exception = assertThrows(
                AppException.class,
                () -> productSellingMeasurementServiceImpl
                        .createSellingMeasurement(requestDto)
        );

        assertThat(exception.getMessage())
                .isEqualTo("Selling measurement already exists.");

        assertThat(exception.getStatus())
                .isEqualTo(HttpStatus.BAD_REQUEST);

        verify(productOptionRepository).findById(1L);

        verify(productSellingMeasurementRepository)
                .existsByProductOption_ProductOptionIdAndMeasurementUnit(
                        1L,
                        MeasurementUnit.DERICA
                );

        verify(productSellingMeasurementRepository, never())
                .save(any(ProductSellingMeasurement.class));
    }

    @Test
    @DisplayName("Should get selling measurement by id")
    void shouldGetSellingMeasurementById() {

        ProductOption productOption = buildProductOption();

        ProductSellingMeasurement measurement =
                ProductSellingMeasurement.builder()
                        .sellingMeasurementId(1L)
                        .productOption(productOption)
                        .measurementUnit(MeasurementUnit.DERICA)
                        .sellingPrice(new BigDecimal("2500.00"))
                        .quantityInStock(20)
                        .enabled(true)
                        .build();

        when(productSellingMeasurementRepository.findById(1L))
                .thenReturn(Optional.of(measurement));

        ProductSellingMeasurementResponseDto response =
                productSellingMeasurementServiceImpl
                        .getSellingMeasurementById(1L);

        assertThat(response).isNotNull();

        assertThat(response.getSellingMeasurementId())
                .isEqualTo(1L);

        assertThat(response.getProductOptionId())
                .isEqualTo(1L);

        assertThat(response.getProductName())
                .isEqualTo("Rice");

        assertThat(response.getProductVariety())
                .isEqualTo("Local Rice");

        assertThat(response.getProductSpecification())
                .isEqualTo("Short Grain");

        assertThat(response.getMeasurementUnit())
                .isEqualTo(MeasurementUnit.DERICA);

        assertThat(response.getSellingPrice())
                .isEqualByComparingTo("2500.00");

        assertThat(response.getQuantityInStock())
                .isEqualTo(20);

        assertThat(response.isEnabled())
                .isTrue();
    }

    @Test
    @DisplayName("Should throw when selling measurement does not exist")
    void shouldThrowWhenSellingMeasurementDoesNotExist() {

        when(productSellingMeasurementRepository.findById(1L))
                .thenReturn(Optional.empty());

        AppException exception = assertThrows(
                AppException.class,
                () -> productSellingMeasurementServiceImpl
                        .getSellingMeasurementById(1L)
        );

        assertThat(exception.getMessage())
                .isEqualTo("Selling measurement not found.");

        assertThat(exception.getStatus())
                .isEqualTo(HttpStatus.NOT_FOUND);

        verify(productSellingMeasurementRepository)
                .findById(1L);
    }

    @Test
    @DisplayName("Should get all selling measurements")
    void shouldGetAllSellingMeasurements() {

        ProductOption productOption = buildProductOption();

        ProductSellingMeasurement firstMeasurement =
                ProductSellingMeasurement.builder()
                        .sellingMeasurementId(1L)
                        .productOption(productOption)
                        .measurementUnit(MeasurementUnit.DERICA)
                        .sellingPrice(new BigDecimal("2500.00"))
                        .quantityInStock(20)
                        .enabled(true)
                        .build();

        ProductSellingMeasurement secondMeasurement =
                ProductSellingMeasurement.builder()
                        .sellingMeasurementId(2L)
                        .productOption(productOption)
                        .measurementUnit(MeasurementUnit.BAG)
                        .sellingPrice(new BigDecimal("95000.00"))
                        .quantityInStock(10)
                        .enabled(true)
                        .build();

        when(productSellingMeasurementRepository.findAll())
                .thenReturn(List.of(firstMeasurement, secondMeasurement));

        List<ProductSellingMeasurementResponseDto> response =
                productSellingMeasurementServiceImpl
                        .getAllSellingMeasurements();

        assertThat(response).hasSize(2);

        assertThat(response.get(0).getSellingMeasurementId())
                .isEqualTo(1L);

        assertThat(response.get(0).getProductName())
                .isEqualTo("Rice");

        assertThat(response.get(0).getMeasurementUnit())
                .isEqualTo(MeasurementUnit.DERICA);

        assertThat(response.get(1).getSellingMeasurementId())
                .isEqualTo(2L);

        assertThat(response.get(1).getMeasurementUnit())
                .isEqualTo(MeasurementUnit.BAG);

        verify(productSellingMeasurementRepository)
                .findAll();

    }

    @Test
    @DisplayName("Should get selling measurements by product option")
    void shouldGetSellingMeasurementsByProductOption() {

        ProductOption productOption = buildProductOption();

        ProductSellingMeasurement firstMeasurement =
                ProductSellingMeasurement.builder()
                        .sellingMeasurementId(1L)
                        .productOption(productOption)
                        .measurementUnit(MeasurementUnit.DERICA)
                        .sellingPrice(new BigDecimal("2500.00"))
                        .quantityInStock(20)
                        .enabled(true)
                        .build();

        ProductSellingMeasurement secondMeasurement =
                ProductSellingMeasurement.builder()
                        .sellingMeasurementId(2L)
                        .productOption(productOption)
                        .measurementUnit(MeasurementUnit.BAG)
                        .sellingPrice(new BigDecimal("95000.00"))
                        .quantityInStock(10)
                        .enabled(true)
                        .build();

        when(productSellingMeasurementRepository
                .findByProductOption_ProductOptionIdAndEnabledTrue(1L))
                .thenReturn(List.of(firstMeasurement, secondMeasurement));

        List<ProductSellingMeasurementResponseDto> response =
                productSellingMeasurementServiceImpl
                        .getSellingMeasurementsByProductOption(1L);

        assertThat(response).hasSize(2);

        assertThat(response.get(0).getSellingMeasurementId())
                .isEqualTo(1L);

        assertThat(response.get(0).getProductName())
                .isEqualTo("Rice");

        assertThat(response.get(0).getMeasurementUnit())
                .isEqualTo(MeasurementUnit.DERICA);

        assertThat(response.get(1).getSellingMeasurementId())
                .isEqualTo(2L);

        assertThat(response.get(1).getMeasurementUnit())
                .isEqualTo(MeasurementUnit.BAG);

        verify(productSellingMeasurementRepository)
                .findByProductOption_ProductOptionIdAndEnabledTrue(1L);
    }


    private ProductOption buildProductOption() {


        Product product = Product.builder()
                .productId(1L)
                .productName("Rice")
                .build();

        return ProductOption.builder()
                .productOptionId(1L)
                .product(product)
                .productVariety("Local Rice")
                .productSpecification("Short Grain")
                .build();
    }


    private ProductSellingMeasurementRequestDto buildRequestDto() {

        ProductSellingMeasurementRequestDto dto =
                new ProductSellingMeasurementRequestDto();

        dto.setProductOptionId(1L);
        dto.setMeasurementUnit(MeasurementUnit.DERICA);
        dto.setSellingPrice(new BigDecimal("2500.00"));
        dto.setQuantityInStock(20);

        return dto;
    }

}