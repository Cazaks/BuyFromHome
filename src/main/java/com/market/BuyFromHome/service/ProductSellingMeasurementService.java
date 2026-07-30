package com.market.BuyFromHome.service;

import com.market.BuyFromHome.dto.requestDto.productSellingRequest.ProductSellingMeasurementRequestDto;
import com.market.BuyFromHome.dto.responseDto.productSellingResponse.ProductSellingMeasurementResponseDto;
import jakarta.transaction.Transactional;

public interface ProductSellingMeasurementService{
    @Transactional
    ProductSellingMeasurementResponseDto createSellingMeasurement(
            ProductSellingMeasurementRequestDto requestDto);
}
