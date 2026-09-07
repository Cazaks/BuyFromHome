package com.market.BuyFromHome.service;

import com.market.BuyFromHome.dto.requestDto.paymentRequest.PaymentRequestDto;
import com.market.BuyFromHome.dto.responseDto.paymentResponse.PaymentResponseDto;
import com.market.BuyFromHome.enums.PaymentStatus;
import com.market.BuyFromHome.exception.AppException;
import com.market.BuyFromHome.model.Order;
import com.market.BuyFromHome.model.Payment;
import com.market.BuyFromHome.model.User;
import com.market.BuyFromHome.repository.OrderRepository;
import com.market.BuyFromHome.repository.PaymentRepository;
import com.market.BuyFromHome.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private static final String DEFAULT_CURRENCY = "NGN";

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    @Transactional
    @Override
    public PaymentResponseDto createPayment(Long userId, PaymentRequestDto requestDto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(
                        "User not found.",
                        HttpStatus.NOT_FOUND
                ));

        Order order = orderRepository.findByOrderIdAndUser_UserId(requestDto.getOrderId(), userId)
                .orElseThrow(() -> new AppException(
                        "Order not found.",
                        HttpStatus.NOT_FOUND
                ));

        Payment payment = Payment.builder()
                .user(user)
                .order(order)
                .amount(order.getTotalAmount())
                .currency(DEFAULT_CURRENCY)
                .paymentMethod(requestDto.getPaymentMethod())
                .status(PaymentStatus.PENDING)
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        return mapToResponse(savedPayment);
    }

    @Transactional
    @Override
    public PaymentResponseDto getPaymentById(Long userId, Long paymentId) {

        Payment payment = paymentRepository.findByPaymentIdAndUser_UserId(paymentId, userId)
                .orElseThrow(() -> new AppException(
                        "Payment not found.",
                        HttpStatus.NOT_FOUND
                ));

        return mapToResponse(payment);
    }

    private PaymentResponseDto mapToResponse(Payment payment) {
        return PaymentResponseDto.builder()
                .paymentId(payment.getPaymentId())
                .orderId(payment.getOrder().getOrderId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .status(payment.getStatus())
                .paymentMethod(payment.getPaymentMethod())
                .gatewayProvider(payment.getGatewayProvider())
                .transactionId(payment.getTransactionId())
                .createdAt(payment.getCreatedAt())
                .paidAt(payment.getPaidAt())
                .build();
    }
}
