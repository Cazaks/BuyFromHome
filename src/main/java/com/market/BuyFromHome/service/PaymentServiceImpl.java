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

import java.time.LocalDateTime;
import java.util.List;

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

        boolean alreadyPaid = paymentRepository.findByOrder_OrderId(order.getOrderId())
                .stream()
                .anyMatch(p -> p.getStatus() == PaymentStatus.SUCCESS);

        if (alreadyPaid) {
            throw new AppException(
                    "This order has already been paid for.",
                    HttpStatus.BAD_REQUEST
            );
        }

        Payment payment = Payment.builder()
                .user(user)
                .order(order)
                .amount(order.getTotalAmount())
                .currency("NGN")
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

    @Transactional
    @Override
    public List<PaymentResponseDto> getPaymentsForUser(Long userId) {
        return paymentRepository.findByUser_UserId(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // NOTE: the three methods below change a payment's state directly and,
    // through it, the order's paid/unpaid status. They must NOT be reachable
    // from a regular authenticated-customer endpoint — only from a verified
    // gateway webhook callback or an admin-only route. Do not wire these to
    // a public controller method without that access control in place.

    @Transactional
    @Override
    public PaymentResponseDto markPaymentProcessing(Long paymentId, String gatewayProvider, String transactionId) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new AppException(
                        "Payment not found.",
                        HttpStatus.NOT_FOUND
                ));

        payment.setStatus(PaymentStatus.PENDING);
        payment.setGatewayProvider(gatewayProvider);
        payment.setTransactionId(transactionId);

        Payment updatedPayment = paymentRepository.save(payment);

        return mapToResponse(updatedPayment);
    }

    @Transactional
    @Override
    public PaymentResponseDto markPaymentSuccess(Long paymentId) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new AppException(
                        "Payment not found.",
                        HttpStatus.NOT_FOUND
                ));

        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setPaidAt(LocalDateTime.now());

        Payment updatedPayment = paymentRepository.save(payment);

        Order order = updatedPayment.getOrder();
        order.setPaymentStatus(PaymentStatus.SUCCESS);
        orderRepository.save(order);

        return mapToResponse(updatedPayment);
    }

    @Transactional
    @Override
    public PaymentResponseDto markPaymentFailed(Long paymentId, String failureReason) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new AppException(
                        "Payment not found.",
                        HttpStatus.NOT_FOUND
                ));

        payment.setStatus(PaymentStatus.FAILED);
        payment.setFailureReason(failureReason);

        Payment updatedPayment = paymentRepository.save(payment);

        Order order = updatedPayment.getOrder();
        order.setPaymentStatus(PaymentStatus.FAILED);
        orderRepository.save(order);

        return mapToResponse(updatedPayment);
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
