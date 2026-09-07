package com.market.BuyFromHome.repository;

import com.market.BuyFromHome.enums.PaymentStatus;
import com.market.BuyFromHome.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByTransactionId(String transactionId);

    // Ownership-scoped lookup, same pattern as
    // AddressRepository.findByAddressIdAndUser_UserId
    Optional<Payment> findByPaymentIdAndUser_UserId(Long paymentId, Long userId);

    List<Payment> findByOrder_OrderId(Long orderId);

    List<Payment> findByUser_UserId(Long userId);

    List<Payment> findByStatus(PaymentStatus status);
}