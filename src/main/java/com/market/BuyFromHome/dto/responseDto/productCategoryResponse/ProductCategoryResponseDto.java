package com.market.BuyFromHome.dto.responseDto.productCategoryResponse;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCategoryResponseDto {

    private Long id;
    private String categoryName;
    private String categoryDescription;
    private boolean enabled;
}
