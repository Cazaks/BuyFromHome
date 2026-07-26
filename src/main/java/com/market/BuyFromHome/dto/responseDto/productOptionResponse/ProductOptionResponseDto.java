package com.market.BuyFromHome.dto.responseDto.productOptionResponse;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

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

    private boolean enabled;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}