package com.market.BuyFromHome.dto.responseDto.productResponse;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ProductResponseDto {

    private Long productId;

    private String productName;

    private String productDescription;

    private Long productCategoryId;

    private String productCategoryName;

    private boolean enabled;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
