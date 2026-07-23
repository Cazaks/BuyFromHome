package com.market.BuyFromHome.dto.requestDto.productOptionRequest;

import com.market.BuyFromHome.enums.MeasurementUnit;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductOptionRequestDto {

    @NotNull(message = "Product is required.")
    private Long productId;

    @Size(max = 100, message = "Product variety cannot exceed 100 characters.")
    private String productVariety;

    @Size(max = 100, message = "Product specification cannot exceed 100 characters.")
    private String productSpecification;

    @NotNull(message = "Measurement unit is required.")
    private MeasurementUnit measurementUnit;

    @NotNull(message = "Product price is required.")
    @DecimalMin(value = "0.01", message = "Product price must be greater than zero.")
    private BigDecimal productPrice;

    @NotNull(message = "Product quantity is required.")
    @Positive(message = "Product quantity must be greater than zero.")
    private Integer productQuantityInStock;
}
