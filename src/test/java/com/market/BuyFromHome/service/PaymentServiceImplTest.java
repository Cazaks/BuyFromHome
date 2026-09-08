package com.market.BuyFromHome.service;

import com.market.BuyFromHome.dto.requestDto.paymentRequest.PaymentRequestDto;
import com.market.BuyFromHome.dto.responseDto.paymentResponse.PaymentResponseDto;
import com.market.BuyFromHome.enums.PaymentMethod;
import com.market.BuyFromHome.enums.PaymentStatus;
import com.market.BuyFromHome.exception.AppException;
import com.market.BuyFromHome.model.Order;
import com.market.BuyFromHome.model.Payment;
import com.market.BuyFromHome.model.User;
import com.market.BuyFromHome.repository.OrderRepository;
import com.market.BuyFromHome.repository.PaymentRepository;
import com.market.BuyFromHome.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private PaymentServiceImpl paymentServiceImpl;
    // ==========================
    // CREATE PAYMENT TESTS
    // ==========================

    @Test
    @DisplayName("Should create payment using the order's own total, ignoring any client amount")
    void shouldCreatePaymentUsingOrderTotalAmount() {

        User user = buildUser();
        Order order = buildOrder(1L, new BigDecimal("50000.00"));
        PaymentRequestDto requestDto = buildRequestDto(order.getOrderId());

        when(userRepository.findById(user.getUserId()))
                .thenReturn(Optional.of(user));

        when(orderRepository.findByOrderIdAndUser_UserId(order.getOrderId(), user.getUserId()))
                .thenReturn(Optional.of(order));

        when(paymentRepository.save(any(Payment.class)))
                .thenAnswer(i -> {
                    Payment payment = i.getArgument(0);
                    payment.setPaymentId(1L);
                    return payment;
                });

        PaymentResponseDto response =
                paymentServiceImpl.createPayment(user.getUserId(), requestDto);

        assertThat(response).isNotNull();
        assertThat(response.getPaymentId()).isEqualTo(1L);
        assertThat(response.getOrderId()).isEqualTo(order.getOrderId());
        assertThat(response.getAmount()).isEqualTo(new BigDecimal("50000.00"));
        assertThat(response.getStatus()).isEqualTo(PaymentStatus.PENDING);

        verify(userRepository).findById(user.getUserId());
        verify(orderRepository).findByOrderIdAndUser_UserId(order.getOrderId(), user.getUserId());
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    @DisplayName("Should throw exception when creating payment for non-existent user")
    void shouldThrowExceptionWhenCreatingPaymentForNonExistentUser() {

        PaymentRequestDto requestDto = buildRequestDto(1L);

        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        AppException exception = assertThrows(
                AppException.class,
                () -> paymentServiceImpl.createPayment(1L, requestDto)
        );

        assertThat(exception.getMessage()).isEqualTo("User not found.");
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);

        verify(orderRepository, never()).findByOrderIdAndUser_UserId(any(), any());
        verify(paymentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when order does not exist or does not belong to the user")
    void shouldThrowExceptionWhenOrderNotFoundOrNotOwnedByUser() {

        User user = buildUser();
        PaymentRequestDto requestDto = buildRequestDto(1L);

        when(userRepository.findById(user.getUserId()))
                .thenReturn(Optional.of(user));

        when(orderRepository.findByOrderIdAndUser_UserId(1L, user.getUserId()))
                .thenReturn(Optional.empty());

        AppException exception = assertThrows(
                AppException.class,
                () -> paymentServiceImpl.createPayment(user.getUserId(), requestDto)
        );

        assertThat(exception.getMessage()).isEqualTo("Order not found.");
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);

        verify(paymentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when order has already been paid for")
    void shouldThrowExceptionWhenOrderAlreadyPaid() {

        User user = buildUser();
        Order order = buildOrder(1L, new BigDecimal("50000.00"));
        PaymentRequestDto requestDto = buildRequestDto(order.getOrderId());

        Payment existingSuccessfulPayment =
                buildPayment(user, order, 1L, PaymentStatus.SUCCESS);

        when(userRepository.findById(user.getUserId()))
                .thenReturn(Optional.of(user));

        when(orderRepository.findByOrderIdAndUser_UserId(order.getOrderId(), user.getUserId()))
                .thenReturn(Optional.of(order));

        when(paymentRepository.findByOrder_OrderId(order.getOrderId()))
                .thenReturn(List.of(existingSuccessfulPayment));

        AppException exception = assertThrows(
                AppException.class,
                () -> paymentServiceImpl.createPayment(user.getUserId(), requestDto)
        );

        assertThat(exception.getMessage()).isEqualTo("This order has already been paid for.");
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);

        verify(paymentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should allow new payment when previous payments exist but none succeeded")
    void shouldAllowNewPaymentWhenPreviousPaymentsFailed() {

        User user = buildUser();
        Order order = buildOrder(1L, new BigDecimal("50000.00"));
        PaymentRequestDto requestDto = buildRequestDto(order.getOrderId());

        Payment failedPayment =
                buildPayment(user, order, 1L, PaymentStatus.FAILED);

        when(userRepository.findById(user.getUserId()))
                .thenReturn(Optional.of(user));

        when(orderRepository.findByOrderIdAndUser_UserId(order.getOrderId(), user.getUserId()))
                .thenReturn(Optional.of(order));

        when(paymentRepository.findByOrder_OrderId(order.getOrderId()))
                .thenReturn(List.of(failedPayment));

        when(paymentRepository.save(any(Payment.class)))
                .thenAnswer(i -> {
                    Payment payment = i.getArgument(0);
                    payment.setPaymentId(2L);
                    return payment;
                });

        PaymentResponseDto response =
                paymentServiceImpl.createPayment(user.getUserId(), requestDto);

        assertThat(response.getStatus()).isEqualTo(PaymentStatus.PENDING);
        verify(paymentRepository).save(any(Payment.class));
    }


    // ==========================
    // GET PAYMENT TESTS
    // ==========================

    @Test
    @DisplayName("Should get payment by id successfully when owned by the user")
    void shouldGetPaymentByIdSuccessfully() {

        User user = buildUser();
        Order order = buildOrder(1L, new BigDecimal("5000.00"));
        Payment payment = buildPayment(user, order, 1L, PaymentStatus.PENDING);

        when(paymentRepository.findByPaymentIdAndUser_UserId(1L, user.getUserId()))
                .thenReturn(Optional.of(payment));

        PaymentResponseDto response =
                paymentServiceImpl.getPaymentById(user.getUserId(), 1L);

        assertThat(response.getPaymentId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should throw exception when payment does not belong to the requesting user")
    void shouldThrowExceptionWhenPaymentDoesNotBelongToUser() {

        when(paymentRepository.findByPaymentIdAndUser_UserId(1L, 99L))
                .thenReturn(Optional.empty());

        AppException exception = assertThrows(
                AppException.class,
                () -> paymentServiceImpl.getPaymentById(99L, 1L)
        );

        assertThat(exception.getMessage()).isEqualTo("Payment not found.");
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Should throw exception when getting non-existent payment")
    void shouldThrowExceptionWhenGettingNonExistentPayment() {

        when(paymentRepository.findByPaymentIdAndUser_UserId(1L, 1L))
                .thenReturn(Optional.empty());

        AppException exception = assertThrows(
                AppException.class,
                () -> paymentServiceImpl.getPaymentById(1L, 1L)
        );

        assertThat(exception.getMessage()).isEqualTo("Payment not found.");
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Should get all payments for a user")
    void shouldGetPaymentsForUser() {

        User user = buildUser();
        Order order = buildOrder(1L, new BigDecimal("5000.00"));
        Payment payment = buildPayment(user, order, 1L, PaymentStatus.SUCCESS);

        when(paymentRepository.findByUser_UserId(user.getUserId()))
                .thenReturn(List.of(payment));

        List<PaymentResponseDto> responses =
                paymentServiceImpl.getPaymentsForUser(user.getUserId());

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getPaymentId()).isEqualTo(1L);
    }

    // ==========================
    // MARK PROCESSING TESTS
    // ==========================

    @Test
    @DisplayName("Should mark payment as processing successfully")
    void shouldMarkPaymentProcessingSuccessfully() {

        User user = buildUser();
        Order order = buildOrder(1L, new BigDecimal("5000.00"));
        Payment payment = buildPayment(user, order, 1L, PaymentStatus.PENDING);

        when(paymentRepository.findById(1L))
                .thenReturn(Optional.of(payment));

        when(paymentRepository.save(any(Payment.class)))
                .thenAnswer(i -> i.getArgument(0));

        PaymentResponseDto response =
                paymentServiceImpl.markPaymentProcessing(1L, "PAYSTACK", "txn_123");

        assertThat(response.getStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(response.getGatewayProvider()).isEqualTo("PAYSTACK");
        assertThat(response.getTransactionId()).isEqualTo("txn_123");

        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when marking non-existent payment as processing")
    void shouldThrowExceptionWhenMarkingNonExistentPaymentProcessing() {

        when(paymentRepository.findById(1L))
                .thenReturn(Optional.empty());

        AppException exception = assertThrows(
                AppException.class,
                () -> paymentServiceImpl.markPaymentProcessing(
                        1L, "PAYSTACK", "txn_123")
        );

        assertThat(exception.getMessage()).isEqualTo("Payment not found.");
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);

        verify(paymentRepository, never()).save(any());
    }

    // ==========================
    // MARK SUCCESS TESTS
    // ==========================


    @Test
    @DisplayName("Should mark payment as success, set paidAt, and sync order payment status")
    void shouldMarkPaymentSuccessSuccessfully() {

        User user = buildUser();
        Order order = buildOrder(1L, new BigDecimal("5000.00"));
        Payment payment = buildPayment(user, order, 1L, PaymentStatus.PENDING);

        when(paymentRepository.findById(1L))
                .thenReturn(Optional.of(payment));

        when(paymentRepository.save(any(Payment.class)))
                .thenAnswer(i -> i.getArgument(0));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(i -> i.getArgument(0));

        PaymentResponseDto response =
                paymentServiceImpl.markPaymentSuccess(1L);

        assertThat(response.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
        assertThat(response.getPaidAt()).isNotNull();
        assertThat(order.getPaymentStatus()).isEqualTo(PaymentStatus.SUCCESS);

        verify(orderRepository).save(order);
    }

    @Test
    @DisplayName("Should throw exception when marking non-existent payment as success")
    void shouldThrowExceptionWhenMarkingNonExistentPaymentSuccess() {

        when(paymentRepository.findById(1L))
                .thenReturn(Optional.empty());

        AppException exception = assertThrows(
                AppException.class,
                () -> paymentServiceImpl.markPaymentSuccess(1L)
        );

        assertThat(exception.getMessage()).isEqualTo("Payment not found.");
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);

        verify(paymentRepository, never()).save(any());
        verify(orderRepository, never()).save(any());
    }

    // ==========================
    // MARK FAILED TESTS
    // ==========================

    @Test
    @DisplayName("Should mark payment as failed with reason and sync order payment status")
    void shouldMarkPaymentFailedSuccessfully() {

        User user = buildUser();
        Order order = buildOrder(1L, new BigDecimal("5000.00"));
        Payment payment = buildPayment(user, order, 1L, PaymentStatus.PENDING);

        when(paymentRepository.findById(1L))
                .thenReturn(Optional.of(payment));

        when(paymentRepository.save(any(Payment.class)))
                .thenAnswer(i -> i.getArgument(0));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(i -> i.getArgument(0));

        PaymentResponseDto response =
                paymentServiceImpl.markPaymentFailed(1L, "Card declined");

        assertThat(response.getStatus()).isEqualTo(PaymentStatus.FAILED);
        assertThat(order.getPaymentStatus()).isEqualTo(PaymentStatus.FAILED);

        verify(orderRepository).save(order);
    }

    @Test
    @DisplayName("Should throw exception when marking non-existent payment as failed")
    void shouldThrowExceptionWhenMarkingNonExistentPaymentFailed() {

        when(paymentRepository.findById(1L))
                .thenReturn(Optional.empty());

        AppException exception = assertThrows(
                AppException.class,
                () -> paymentServiceImpl.markPaymentFailed(1L, "Card declined")
        );

        assertThat(exception.getMessage()).isEqualTo("Payment not found.");
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);

        verify(paymentRepository, never()).save(any());
        verify(orderRepository, never()).save(any());
    }



    // ==========================
    // TEST HELPERS
    // ==========================

    private User buildUser() {
        return User.builder()
                .userId(1L)
                .firstName("Caleb")
                .lastName("Osowo")
                .email("caleb@test.com")
                .build();
    }

    private Order buildOrder(Long orderId, BigDecimal totalAmount) {
        return Order.builder()
                .orderId(orderId)
                .orderNumber("ORD-" + orderId)
                .totalAmount(totalAmount)
                .paymentMethod(PaymentMethod.CARD)
                .build();
    }

    private Payment buildPayment(User user, Order order, Long paymentId, PaymentStatus status) {
        return Payment.builder()
                .paymentId(paymentId)
                .user(user)
                .order(order)
                .amount(order.getTotalAmount())
                .currency("NGN")
                .paymentMethod(PaymentMethod.CARD)
                .status(status)
                .build();
    }

    private PaymentRequestDto buildRequestDto(Long orderId) {
        return PaymentRequestDto.builder()
                .orderId(orderId)
                .paymentMethod(PaymentMethod.CARD)
                .build();
    }

}