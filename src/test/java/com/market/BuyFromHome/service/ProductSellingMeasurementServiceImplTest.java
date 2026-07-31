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
    @DisplayName("Should throw exception when selling measurement does not exist")
    void shouldThrowExceptionWhenSellingMeasurementDoesNotExist() {

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

        assertThat(response.getFirst().getSellingMeasurementId())
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
    @DisplayName("Should throw exception when no selling measurements are available")
    void shouldThrowExceptionWhenNoSellingMeasurementsAreAvailable() {

        when(productSellingMeasurementRepository.findAll())
                .thenReturn(List.of());

        AppException exception = assertThrows(
                AppException.class,
                () -> productSellingMeasurementServiceImpl
                        .getAllSellingMeasurements()
        );

        assertThat(exception.getMessage())
                .isEqualTo("No selling measurements available.");

        assertThat(exception.getStatus())
                .isEqualTo(HttpStatus.NOT_FOUND);

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

        assertThat(response.getFirst().getSellingMeasurementId())
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

    @Test
    @DisplayName("Should return only enabled selling measurements by product option")
    void shouldReturnOnlyEnabledSellingMeasurementsByProductOption() {

        ProductOption productOption = buildProductOption();

        ProductSellingMeasurement enabledMeasurement =
                ProductSellingMeasurement.builder()
                        .sellingMeasurementId(1L)
                        .productOption(productOption)
                        .measurementUnit(MeasurementUnit.DERICA)
                        .sellingPrice(new BigDecimal("2500.00"))
                        .quantityInStock(20)
                        .enabled(true)
                        .build();

        when(productSellingMeasurementRepository
                .findByProductOption_ProductOptionIdAndEnabledTrue(1L))
                .thenReturn(List.of(enabledMeasurement));

        List<ProductSellingMeasurementResponseDto> response =
                productSellingMeasurementServiceImpl
                        .getSellingMeasurementsByProductOption(1L);

        assertThat(response).hasSize(1);

        assertThat(response.getFirst().getSellingMeasurementId())
                .isEqualTo(1L);

        assertThat(response.getFirst().isEnabled())
                .isTrue();

        verify(productSellingMeasurementRepository)
                .findByProductOption_ProductOptionIdAndEnabledTrue(1L);
    }

    @Test
    @DisplayName("Should throw exception when no selling measurements are available for product option")
    void shouldThrowExceptionWhenNoSellingMeasurementsAreAvailableForProductOption() {

        when(productSellingMeasurementRepository
                .findByProductOption_ProductOptionIdAndEnabledTrue(1L))
                .thenReturn(List.of());

        AppException exception = assertThrows(
                AppException.class,
                () -> productSellingMeasurementServiceImpl
                        .getSellingMeasurementsByProductOption(1L)
        );

        assertThat(exception.getMessage())
                .isEqualTo("No selling measurements available for this product option.");

        assertThat(exception.getStatus())
                .isEqualTo(HttpStatus.NOT_FOUND);

        verify(productSellingMeasurementRepository)
                .findByProductOption_ProductOptionIdAndEnabledTrue(1L);
    }

//    @Test
//    @DisplayName("Should update selling measurement successfully")
//    void shouldUpdateSellingMeasurementSuccessfully() {
//
//        ProductOption productOption = buildProductOption();
//
//        ProductSellingMeasurement existingMeasurement =
//                ProductSellingMeasurement.builder()
//                        .sellingMeasurementId(1L)
//                        .productOption(productOption)
//                        .measurementUnit(MeasurementUnit.DERICA)
//                        .sellingPrice(new BigDecimal("2500.00"))
//                        .quantityInStock(20)
//                        .enabled(true)
//                        .build();
//
//        ProductSellingMeasurementRequestDto requestDto =
//                buildRequestDto(
//                        new BigDecimal("95000.00")
//                );
//
//        when(productSellingMeasurementRepository.findById(1L))
//                .thenReturn(Optional.of(existingMeasurement));
//
//        when(productSellingMeasurementRepository
//                .existsByProductOption_ProductOptionIdAndMeasurementUnit(
//                        1L,
//                        MeasurementUnit.BAG
//                ))
//                .thenReturn(false);
//
//        when(productSellingMeasurementRepository.save(any(
//                ProductSellingMeasurement.class)))
//                .thenAnswer(invocation -> invocation.getArgument(0));
//
//        ProductSellingMeasurementResponseDto response =
//                productSellingMeasurementServiceImpl
//                        .updateSellingMeasurement(1L, requestDto);
//
//        assertThat(response.getSellingMeasurementId())
//                .isEqualTo(1L);
//
//        assertThat(response.getMeasurementUnit())
//                .isEqualTo(MeasurementUnit.BAG);
//
//        assertThat(response.getSellingPrice())
//                .isEqualByComparingTo("95000.00");
//
//        assertThat(response.getQuantityInStock())
//                .isEqualTo(10);
//
//        assertThat(response.isEnabled())
//                .isTrue();
//
//        verify(productSellingMeasurementRepository)
//                .save(existingMeasurement);
//    }

    @Test
    @DisplayName("Should update selling measurement successfully with same measurement unit")
    void shouldUpdateSellingMeasurementSuccessfullyWithSameMeasurementUnit() {

        ProductOption productOption = buildProductOption();

        ProductSellingMeasurement existingMeasurement =
                ProductSellingMeasurement.builder()
                        .sellingMeasurementId(1L)
                        .productOption(productOption)
                        .measurementUnit(MeasurementUnit.DERICA)
                        .sellingPrice(new BigDecimal("2500.00"))
                        .quantityInStock(20)
                        .enabled(true)
                        .build();

        ProductSellingMeasurementRequestDto requestDto =
                new ProductSellingMeasurementRequestDto();

        requestDto.setProductOptionId(1L);
        requestDto.setMeasurementUnit(MeasurementUnit.DERICA);
        requestDto.setSellingPrice(new BigDecimal("3000.00"));
        requestDto.setQuantityInStock(30);

        when(productSellingMeasurementRepository.findById(1L))
                .thenReturn(Optional.of(existingMeasurement));

        when(productSellingMeasurementRepository
                .existsByProductOption_ProductOptionIdAndMeasurementUnit(
                        1L,
                        MeasurementUnit.DERICA
                ))
                .thenReturn(true);

        when(productSellingMeasurementRepository.save(
                any(ProductSellingMeasurement.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProductSellingMeasurementResponseDto response =
                productSellingMeasurementServiceImpl
                        .updateSellingMeasurement(1L, requestDto);

        assertThat(response.getMeasurementUnit())
                .isEqualTo(MeasurementUnit.DERICA);

        assertThat(response.getSellingPrice())
                .isEqualByComparingTo("3000.00");

        assertThat(response.getQuantityInStock())
                .isEqualTo(30);

        verify(productSellingMeasurementRepository)
                .save(existingMeasurement);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existing selling measurement")
    void shouldThrowExceptionWhenUpdatingNonExistingSellingMeasurement() {

        ProductSellingMeasurementRequestDto requestDto =
                buildRequestDto();

        when(productSellingMeasurementRepository.findById(1L))
                .thenReturn(Optional.empty());

        AppException exception = assertThrows(
                AppException.class,
                () -> productSellingMeasurementServiceImpl
                        .updateSellingMeasurement(1L, requestDto)
        );

        assertThat(exception.getMessage())
                .isEqualTo("Selling measurement not found.");

        assertThat(exception.getStatus())
                .isEqualTo(HttpStatus.NOT_FOUND);

        verify(productSellingMeasurementRepository)
                .findById(1L);

        verify(productSellingMeasurementRepository, never())
                .existsByProductOption_ProductOptionIdAndMeasurementUnit(
                        anyLong(),
                        any()
                );

        verify(productSellingMeasurementRepository, never())
                .save(any(ProductSellingMeasurement.class));
    }

    @Test
    @DisplayName("Should throw exception when updating to an existing measurement unit")
    void shouldThrowExceptionWhenUpdatingToAnExistingMeasurementUnit() {

        ProductOption productOption = buildProductOption();

        ProductSellingMeasurement existingMeasurement =
                ProductSellingMeasurement.builder()
                        .sellingMeasurementId(1L)
                        .productOption(productOption)
                        .measurementUnit(MeasurementUnit.DERICA)
                        .sellingPrice(new BigDecimal("2500.00"))
                        .quantityInStock(20)
                        .enabled(true)
                        .build();

        ProductSellingMeasurementRequestDto requestDto =
                buildRequestDto(
                        MeasurementUnit.BAG,
                        new BigDecimal("95000.00"),
                        10
                );

        when(productSellingMeasurementRepository.findById(1L))
                .thenReturn(Optional.of(existingMeasurement));

        when(productSellingMeasurementRepository
                .existsByProductOption_ProductOptionIdAndMeasurementUnit(
                        1L,
                        MeasurementUnit.BAG
                ))
                .thenReturn(true);

        AppException exception = assertThrows(
                AppException.class,
                () -> productSellingMeasurementServiceImpl
                        .updateSellingMeasurement(1L, requestDto)
        );

        assertThat(exception.getMessage())
                .isEqualTo("Selling measurement already exists.");

        assertThat(exception.getStatus())
                .isEqualTo(HttpStatus.BAD_REQUEST);

        verify(productSellingMeasurementRepository)
                .findById(1L);

        verify(productSellingMeasurementRepository)
                .existsByProductOption_ProductOptionIdAndMeasurementUnit(
                        1L,
                        MeasurementUnit.BAG
                );

        verify(productSellingMeasurementRepository, never())
                .save(any(ProductSellingMeasurement.class));
    }
    @Test
    @DisplayName("Should disable selling measurement successfully")
    void shouldDisableSellingMeasurementSuccessfully() {

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

        when(productSellingMeasurementRepository.save(
                any(ProductSellingMeasurement.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProductSellingMeasurementResponseDto response =
                productSellingMeasurementServiceImpl
                        .disableSellingMeasurement(1L);

        assertThat(response).isNotNull();

        assertThat(response.getSellingMeasurementId())
                .isEqualTo(1L);

        assertThat(response.isEnabled())
                .isFalse();

        verify(productSellingMeasurementRepository)
                .findById(1L);

        verify(productSellingMeasurementRepository)
                .save(measurement);
    }

    @Test
    @DisplayName("Should throw exception when disabling non-existing selling measurement")
    void shouldThrowExceptionWhenDisablingNonExistingSellingMeasurement() {

        when(productSellingMeasurementRepository.findById(1L))
                .thenReturn(Optional.empty());

        AppException exception = assertThrows(
                AppException.class,
                () -> productSellingMeasurementServiceImpl
                        .disableSellingMeasurement(1L)
        );

        assertThat(exception.getMessage())
                .isEqualTo("Selling measurement not found.");

        assertThat(exception.getStatus())
                .isEqualTo(HttpStatus.NOT_FOUND);

        verify(productSellingMeasurementRepository)
                .findById(1L);

        verify(productSellingMeasurementRepository, never())
                .save(any(ProductSellingMeasurement.class));
    }

    @Test
    @DisplayName("Should enable selling measurement successfully")
    void shouldEnableSellingMeasurementSuccessfully() {

        ProductOption productOption = buildProductOption();

        ProductSellingMeasurement measurement =
                ProductSellingMeasurement.builder()
                        .sellingMeasurementId(1L)
                        .productOption(productOption)
                        .measurementUnit(MeasurementUnit.DERICA)
                        .sellingPrice(new BigDecimal("2500.00"))
                        .quantityInStock(20)
                        .enabled(false)
                        .build();

        when(productSellingMeasurementRepository.findById(1L))
                .thenReturn(Optional.of(measurement));

        when(productSellingMeasurementRepository.save(
                any(ProductSellingMeasurement.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProductSellingMeasurementResponseDto response =
                productSellingMeasurementServiceImpl
                        .enableSellingMeasurement(1L);

        assertThat(response).isNotNull();

        assertThat(response.getSellingMeasurementId())
                .isEqualTo(1L);

        assertThat(response.isEnabled())
                .isTrue();

        verify(productSellingMeasurementRepository)
                .findById(1L);

        verify(productSellingMeasurementRepository)
                .save(measurement);
    }

    @Test
    @DisplayName("Should throw when enabling non-existing selling measurement")
    void shouldThrowWhenEnablingNonExistingSellingMeasurement() {

        when(productSellingMeasurementRepository.findById(1L))
                .thenReturn(Optional.empty());

        AppException exception = assertThrows(
                AppException.class,
                () -> productSellingMeasurementServiceImpl
                        .enableSellingMeasurement(1L)
        );

        assertThat(exception.getMessage())
                .isEqualTo("Selling measurement not found.");

        assertThat(exception.getStatus())
                .isEqualTo(HttpStatus.NOT_FOUND);

        verify(productSellingMeasurementRepository)
                .findById(1L);

        verify(productSellingMeasurementRepository, never())
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

    private ProductSellingMeasurementRequestDto buildRequestDto(
            MeasurementUnit measurementUnit,
            BigDecimal sellingPrice,
            Integer quantityInStock) {

        ProductSellingMeasurementRequestDto dto =
                new ProductSellingMeasurementRequestDto();

        dto.setProductOptionId(1L);
        dto.setMeasurementUnit(measurementUnit);
        dto.setSellingPrice(sellingPrice);
        dto.setQuantityInStock(quantityInStock);

        return dto;
    }

}