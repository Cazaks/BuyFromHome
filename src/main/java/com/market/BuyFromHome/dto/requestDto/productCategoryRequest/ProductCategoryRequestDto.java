package com.market.BuyFromHome.dto.requestDto.productCategoryRequest;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductCategoryRequestDto {

    @NotBlank(message = "Category name is required")
    private String name;

    private String description;
}
