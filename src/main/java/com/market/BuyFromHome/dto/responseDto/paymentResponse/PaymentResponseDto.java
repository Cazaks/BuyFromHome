package com.market.BuyFromHome.dto.responseDto.paymentResponse;

import com.market.BuyFromHome.enums.PaymentMethod;
import com.market.BuyFromHome.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponseDto {

    private Long paymentId;

    private Long orderId;

    private BigDecimal amount;

    private String currency;

    private PaymentStatus status;

    private PaymentMethod paymentMethod;

    private String gatewayProvider;

    private String transactionId;

    private LocalDateTime createdAt;

    private LocalDateTime paidAt;
}