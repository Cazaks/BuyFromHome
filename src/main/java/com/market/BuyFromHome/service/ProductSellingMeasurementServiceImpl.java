package com.market.BuyFromHome.service;

import com.market.BuyFromHome.dto.requestDto.productSellingRequest.ProductSellingMeasurementRequestDto;
import com.market.BuyFromHome.dto.responseDto.productSellingResponse.ProductSellingMeasurementResponseDto;
import com.market.BuyFromHome.exception.AppException;
import com.market.BuyFromHome.model.ProductOption;
import com.market.BuyFromHome.model.ProductSellingMeasurement;
import com.market.BuyFromHome.repository.ProductOptionRepository;
import com.market.BuyFromHome.repository.ProductSellingMeasurementRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductSellingMeasurementServiceImpl implements ProductSellingMeasurementService{

    private final ProductOptionRepository productOptionRepository;
    private final ProductSellingMeasurementRepository productSellingMeasurementRepository;

    @Transactional
    @Override
    public ProductSellingMeasurementResponseDto createSellingMeasurement(
            ProductSellingMeasurementRequestDto requestDto){

        ProductOption productOption = productOptionRepository.findById(
                requestDto.getProductOptionId()
        ).orElseThrow(() ->
                new AppException("Product of not found.",
                        HttpStatus.NOT_FOUND)
        );

        boolean exists =
                productSellingMeasurementRepository
                        .existsByProductOption_ProductOptionIdAndMeasurementUnit(
                                productOption.getProductOptionId(),
                                requestDto.getMeasurementUnit()
                        );

        if (exists) {
            throw new AppException(
                    "Selling measurement already exists.",
                    HttpStatus.BAD_REQUEST
            );
        }

        ProductSellingMeasurement measurement =
                ProductSellingMeasurement.builder()
                        .productOption(productOption)
                        .measurementUnit(requestDto.getMeasurementUnit())
                        .sellingPrice(requestDto.getSellingPrice())
                        .quantityInStock(requestDto.getQuantityInStock())
                        .build();

        ProductSellingMeasurement savedMeasurement =
                productSellingMeasurementRepository.save(measurement);

        return mapToResponse(savedMeasurement);


    }

    private ProductSellingMeasurementResponseDto mapToResponse(
            ProductSellingMeasurement measurement){

        ProductOption productOption = measurement.getProductOption();
        return ProductSellingMeasurementResponseDto.builder()
                .sellingMeasurementId(
                        measurement.getSellingMeasurementId()
                )

                .productOptionId(
                        productOption.getProductOptionId()
                )

                .productName(productOption.getProduct().getProductName())

                .productVariety(
                        productOption.getProductVariety()
                )

                .productSpecification(
                        productOption.getProductSpecification()
                )

                .measurementUnit(
                        measurement.getMeasurementUnit()
                )

                .sellingPrice(
                        measurement.getSellingPrice()
                )

                .quantityInStock(
                        measurement.getQuantityInStock()
                )

                .enabled(
                        measurement.isEnabled()
                )

                .createdAt(
                        measurement.getCreatedAt()
                )

                .updatedAt(
                        measurement.getUpdatedAt()
                )
                .build();



    }

}
