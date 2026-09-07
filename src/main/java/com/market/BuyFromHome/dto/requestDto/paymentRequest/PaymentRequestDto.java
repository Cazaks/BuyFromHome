package com.market.BuyFromHome.dto.requestDto.paymentRequest;

import com.market.BuyFromHome.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequestDto {

    // amount and currency are intentionally NOT here — the amount to
    // charge always comes from order.getTotalAmount() on the server,
    // never from client input. Trusting a client-supplied amount lets
    // a user pay whatever they want for any order.

    @NotNull(message = "Order id is required")
    private Long orderId;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;
}