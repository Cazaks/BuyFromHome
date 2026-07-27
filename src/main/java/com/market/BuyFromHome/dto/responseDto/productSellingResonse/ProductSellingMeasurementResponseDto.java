package com.market.BuyFromHome.dto.responseDto.productSellingResonse;

import com.market.BuyFromHome.enums.MeasurementUnit;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ProductSellingMeasurementResponseDto {

    private Long sellingMeasurementId;

    private Long productOptionId;

    private String productName;

    private String productVariety;

    private String productSpecification;

    private MeasurementUnit measurementUnit;

    private BigDecimal sellingPrice;

    private Integer quantityInStock;

    private boolean enabled;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}