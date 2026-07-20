package com.market.BuyFromHome.dto.responseDto.productCategoryResponse;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductCategoryResponseDto {

    private Long id;
    private String name;
    private String description;
    private boolean enabled;
}
