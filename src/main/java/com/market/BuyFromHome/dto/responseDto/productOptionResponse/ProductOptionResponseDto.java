package com.market.BuyFromHome.dto.responseDto.productOptionResponse;

import com.market.BuyFromHome.enums.MeasurementUnit;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ProductOptionResponseDto {


    private Long productOptionId;

    private Long productId;

    private String productName;

    private String productVariety;

    private String productSpecification;

    private MeasurementUnit measurementUnit;

    private BigDecimal productPrice;

    private Integer productQuantityInStock;

    private boolean enabled;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
