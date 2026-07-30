package com.market.BuyFromHome.service;

import com.market.BuyFromHome.dto.requestDto.productSellingRequest.ProductSellingMeasurementRequestDto;
import com.market.BuyFromHome.dto.responseDto.productSellingResponse.ProductSellingMeasurementResponseDto;
import com.market.BuyFromHome.enums.MeasurementUnit;
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

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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