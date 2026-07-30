package com.market.BuyFromHome.service;

import com.market.BuyFromHome.dto.requestDto.productSellingRequest.ProductSellingMeasurementRequestDto;
import com.market.BuyFromHome.dto.responseDto.productSellingResponse.ProductSellingMeasurementResponseDto;
import com.market.BuyFromHome.exception.AppException;
import com.market.BuyFromHome.model.ProductOption;
import com.market.BuyFromHome.model.ProductSellingMeasurement;
import com.market.BuyFromHome.repository.ProductOptionRepository;
import com.market.BuyFromHome.repository.ProductSellingMeasurementRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

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
                new AppException("Product option not found.",
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

    @Transactional(readOnly = true)
    @Override
    public ProductSellingMeasurementResponseDto getSellingMeasurementById(
            Long sellingMeasurementId) {

        ProductSellingMeasurement measurement =
                productSellingMeasurementRepository
                        .findById(sellingMeasurementId)
                        .orElseThrow(() ->
                                new AppException(
                                        "Selling measurement not found.",
                                        HttpStatus.NOT_FOUND
                                ));

        return mapToResponse(measurement);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ProductSellingMeasurementResponseDto> getAllSellingMeasurements() {

        List<ProductSellingMeasurement> measurements =
                productSellingMeasurementRepository.findAll();

        if (measurements.isEmpty()) {
            throw new AppException(
                    "No selling measurements available.",
                    HttpStatus.NOT_FOUND
            );
        }

        return measurements.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<ProductSellingMeasurementResponseDto> getSellingMeasurementsByProductOption(
            Long productOptionId) {

        return productSellingMeasurementRepository
                .findByProductOption_ProductOptionIdAndEnabledTrue(productOptionId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    @Override
    public ProductSellingMeasurementResponseDto updateSellingMeasurement(
            Long sellingMeasurementId,
            ProductSellingMeasurementRequestDto requestDto) {

        ProductSellingMeasurement measurement =
                productSellingMeasurementRepository
                        .findById(sellingMeasurementId)
                        .orElseThrow(() ->
                                new AppException(
                                        "Selling measurement not found.",
                                        HttpStatus.NOT_FOUND
                                ));

        boolean exists =
                productSellingMeasurementRepository
                        .existsByProductOption_ProductOptionIdAndMeasurementUnit(
                                measurement.getProductOption().getProductOptionId(),
                                requestDto.getMeasurementUnit()
                        );

        if (exists &&
                !measurement.getMeasurementUnit()
                        .equals(requestDto.getMeasurementUnit())) {

            throw new AppException(
                    "Selling measurement already exists.",
                    HttpStatus.BAD_REQUEST
            );
        }

        measurement.setMeasurementUnit(
                requestDto.getMeasurementUnit()
        );

        measurement.setSellingPrice(
                requestDto.getSellingPrice()
        );

        measurement.setQuantityInStock(
                requestDto.getQuantityInStock()
        );

        ProductSellingMeasurement updatedMeasurement =
                productSellingMeasurementRepository
                        .save(measurement);

        return mapToResponse(updatedMeasurement);
    }

    @Transactional
    @Override
    public ProductSellingMeasurementResponseDto disableSellingMeasurement(
            Long sellingMeasurementId) {

        ProductSellingMeasurement measurement =
                productSellingMeasurementRepository
                        .findById(sellingMeasurementId)
                        .orElseThrow(() ->
                                new AppException(
                                        "Selling measurement not found.",
                                        HttpStatus.NOT_FOUND
                                ));

        measurement.setEnabled(false);

        ProductSellingMeasurement disabledMeasurement =
                productSellingMeasurementRepository.save(measurement);

        return mapToResponse(disabledMeasurement);
    }

    @Transactional
    @Override
    public ProductSellingMeasurementResponseDto enableSellingMeasurement(
            Long sellingMeasurementId) {

        ProductSellingMeasurement measurement =
                productSellingMeasurementRepository
                        .findById(sellingMeasurementId)
                        .orElseThrow(() ->
                                new AppException(
                                        "Selling measurement not found.",
                                        HttpStatus.NOT_FOUND
                                ));

        measurement.setEnabled(true);

        ProductSellingMeasurement enabledMeasurement =
                productSellingMeasurementRepository.save(measurement);

        return mapToResponse(enabledMeasurement);
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
