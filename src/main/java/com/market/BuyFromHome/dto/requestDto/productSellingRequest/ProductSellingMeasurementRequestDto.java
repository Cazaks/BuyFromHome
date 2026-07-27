package com.market.BuyFromHome.dto.requestDto.productSellingRequest;

import com.market.BuyFromHome.enums.MeasurementUnit;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductSellingMeasurementRequestDto {

    @NotNull(message = "Product option is required.")
    private Long productOptionId;

    @NotNull(message = "Measurement unit is required.")
    private MeasurementUnit measurementUnit;

    @NotNull(message = "Selling price is required.")
    @DecimalMin(value = "0.01",
            message = "Selling price must be greater than zero.")
    private BigDecimal sellingPrice;

    @NotNull(message = "Quantity in stock is required.")
    @Positive(message = "Quantity in stock must be greater than zero.")
    private Integer quantityInStock;
}