package com.market.BuyFromHome.service;

import com.market.BuyFromHome.dto.requestDto.paymentRequest.PaymentRequestDto;
import com.market.BuyFromHome.dto.responseDto.paymentResponse.PaymentResponseDto;
import jakarta.transaction.Transactional;

public interface PaymentService {
    @Transactional
    PaymentResponseDto createPayment(Long userId, PaymentRequestDto requestDto);
}
