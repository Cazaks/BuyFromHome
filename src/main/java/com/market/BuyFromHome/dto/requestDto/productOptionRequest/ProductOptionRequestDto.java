package com.market.BuyFromHome.dto.requestDto.productOptionRequest;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductOptionRequestDto {

    @NotNull(message = "Product is required.")
    private Long productId;

    @Size(max = 100, message = "Product variety cannot exceed 100 characters.")
    private String productVariety;

    @Size(max = 100, message = "Product specification cannot exceed 100 characters.")
    private String productSpecification;
}