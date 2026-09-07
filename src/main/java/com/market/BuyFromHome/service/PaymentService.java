package com.market.BuyFromHome.service;

import com.market.BuyFromHome.dto.requestDto.paymentRequest.PaymentRequestDto;
import com.market.BuyFromHome.dto.responseDto.paymentResponse.PaymentResponseDto;
import jakarta.transaction.Transactional;

public interface PaymentService {
    @Transactional
    PaymentResponseDto createPayment(Long userId, PaymentRequestDto requestDto);

    @Transactional
    PaymentResponseDto getPaymentById(Long userId, Long paymentId);

    @Transactional
    PaymentResponseDto markPaymentProcessing(Long paymentId, String gatewayProvider, String transactionId);
}
