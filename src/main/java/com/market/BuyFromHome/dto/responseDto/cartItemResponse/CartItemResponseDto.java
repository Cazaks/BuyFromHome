package com.market.BuyFromHome.dto.responseDto.cartItemResponse;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponseDto {

    private Long cartItemId;

    private Long categoryId;
    private String categoryName;

    private Long productId;
    private String productName;

    private Long productOptionId;
    private String productVariety;
    private String productSpecification;

    private Long sellingMeasurementId;
    private String measurementUnit;

    private Integer quantity;

    private BigDecimal priceAtTimeOfAdding;

    private BigDecimal subtotal;
}
