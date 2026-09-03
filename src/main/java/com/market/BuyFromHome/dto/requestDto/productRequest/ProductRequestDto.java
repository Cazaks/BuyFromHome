package com.market.BuyFromHome.dto.requestDto.productRequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductRequestDto {

    @NotBlank(message = "Product name is required.")
    @Size(max = 150, message = "Product name cannot exceed 150 characters.")
    private String productName;

    @NotBlank(message = "Product description is required.")
    @Size(max = 3000, message = "Product description cannot exceed 3000 characters.")
    private String productDescription;

    private String imageUrl;

    @NotNull(message = "Product category is required.")
    private Long productCategoryId;
}
