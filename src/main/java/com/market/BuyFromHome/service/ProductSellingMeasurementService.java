package com.market.BuyFromHome.service;

import com.market.BuyFromHome.dto.requestDto.productSellingRequest.ProductSellingMeasurementRequestDto;
import com.market.BuyFromHome.dto.responseDto.productSellingResponse.ProductSellingMeasurementResponseDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ProductSellingMeasurementService{
    @Transactional
    ProductSellingMeasurementResponseDto createSellingMeasurement(
            ProductSellingMeasurementRequestDto requestDto);

    @Transactional(readOnly = true)
    ProductSellingMeasurementResponseDto getSellingMeasurementById(
            Long sellingMeasurementId);

    @Transactional(readOnly = true)
    List<ProductSellingMeasurementResponseDto> getAllSellingMeasurements();

    @Transactional(readOnly = true)
    List<ProductSellingMeasurementResponseDto> getSellingMeasurementsByProductOption(
            Long productOptionId);

    @Transactional
    ProductSellingMeasurementResponseDto updateSellingMeasurement(
            Long sellingMeasurementId,
            ProductSellingMeasurementRequestDto requestDto);
}
