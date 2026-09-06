package com.market.BuyFromHome.dto.requestDto.orderRequest;

import com.market.BuyFromHome.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDto {

    @NotNull(message = "Delivery address is required")
    private Long addressId;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    @Size(
            max = 1000,
            message = "Notes cannot exceed 1000 characters"
    )
    private String notes;
}