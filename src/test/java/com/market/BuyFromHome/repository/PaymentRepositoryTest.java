package com.market.BuyFromHome.repository;

import com.market.BuyFromHome.config.ApplicationConfig;
import com.market.BuyFromHome.enums.AuthProvider;
import com.market.BuyFromHome.enums.PaymentMethod;
import com.market.BuyFromHome.enums.PaymentStatus;
import com.market.BuyFromHome.enums.Role;
import com.market.BuyFromHome.model.Order;
import com.market.BuyFromHome.model.Payment;
import com.market.BuyFromHome.model.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(ApplicationConfig.class)
class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private EntityManager entityManager;

    @Test
    @DisplayName("Should find payment by payment id and user id")
    void shouldFindPaymentByIdAndUserId() {
        User user = userRepository.save(buildUser("owner@test.com"));
        Order order = orderRepository.save(buildOrder(user));
        Payment payment = paymentRepository.save(buildPayment(user, order, PaymentStatus.PENDING));
        entityManager.flush();
        entityManager.clear();

        Optional<Payment> result = paymentRepository
                .findByPaymentIdAndUser_UserId(payment.getPaymentId(), user.getUserId());

        assertThat(result).isPresent();
        assertThat(result.get().getPaymentId()).isEqualTo(payment.getPaymentId());
    }

    @Test
    @DisplayName("Should not return a payment that belongs to another user")
    void shouldNotFindPaymentOfAnotherUser() {
        User owner = userRepository.save(buildUser("owner@test.com"));
        User stranger = userRepository.save(buildUser("stranger@test.com"));
        Order order = orderRepository.save(buildOrder(owner));
        Payment payment = paymentRepository.save(buildPayment(owner, order, PaymentStatus.PENDING));
        entityManager.flush();
        entityManager.clear();

        Optional<Payment> result = paymentRepository
                .findByPaymentIdAndUser_UserId(payment.getPaymentId(), stranger.getUserId());

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should find only the payments of the given order")
    void shouldFindPaymentsByOrderId() {
        User user = userRepository.save(buildUser("owner@test.com"));
        Order order1 = orderRepository.save(buildOrder(user));
        Order order2 = orderRepository.save(buildOrder(user));
        paymentRepository.save(buildPayment(user, order1, PaymentStatus.FAILED));
        paymentRepository.save(buildPayment(user, order1, PaymentStatus.SUCCESS));
        paymentRepository.save(buildPayment(user, order2, PaymentStatus.PENDING));
        entityManager.flush();
        entityManager.clear();

        List<Payment> result = paymentRepository.findByOrder_OrderId(order1.getOrderId());

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("Should find only the payments of the given user")
    void shouldFindPaymentsByUserId() {
        User user1 = userRepository.save(buildUser("one@test.com"));
        User user2 = userRepository.save(buildUser("two@test.com"));
        Order order1 = orderRepository.save(buildOrder(user1));
        Order order2 = orderRepository.save(buildOrder(user2));
        paymentRepository.save(buildPayment(user1, order1, PaymentStatus.PENDING));
        paymentRepository.save(buildPayment(user2, order2, PaymentStatus.PENDING));
        entityManager.flush();
        entityManager.clear();

        List<Payment> result = paymentRepository.findByUser_UserId(user1.getUserId());

        assertThat(result).hasSize(1);
    }

    private User buildUser(String email) {
        return User.builder()
                .firstName("John").lastName("Doe").email(email)
                .password("password").phoneNumber("08012345678")
                .role(Role.CUSTOMER).provider(AuthProvider.LOCAL).enabled(true)
                .build();
    }

    private Order buildOrder(User user) {
        return Order.builder()
                .user(user)
                .orderNumber("ORD-" + System.nanoTime())
                .totalAmount(new BigDecimal("5000.00"))
                .paymentMethod(PaymentMethod.CARD)
                .build();
    }

    private Payment buildPayment(User user, Order order, PaymentStatus status) {
        return Payment.builder()
                .user(user).order(order)
                .amount(order.getTotalAmount()).currency("NGN")
                .paymentMethod(PaymentMethod.CARD).status(status)
                .build();
    }
}