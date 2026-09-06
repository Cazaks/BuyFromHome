package com.market.BuyFromHome.dto.responseDto.orderItemResponse;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponseDto {

    private Long orderItemId;

    // Product
    private Long productId;
    private String productName;

    // Product option
    private Long productOptionId;
    private String productVariety;
    private String productSpecification;

    // Selling measurement
    private Long sellingMeasurementId;
    private String measurementUnit;

    // Order information
    private Integer quantity;

    private BigDecimal unitPrice;

    private BigDecimal subtotal;
}