package com.market.BuyFromHome.controller;

import com.market.BuyFromHome.dto.requestDto.paymentRequest.PaymentRequestDto;
import com.market.BuyFromHome.dto.responseDto.paymentResponse.PaymentResponseDto;
import com.market.BuyFromHome.security.CurrentUserProvider;
import com.market.BuyFromHome.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(
        name = "Payments",
        description = "Endpoints for managing order payments"
)
public class PaymentController {

    private final PaymentService paymentService;
    private final CurrentUserProvider currentUserProvider;

    @PostMapping
    @Operation(summary = "Initiate a payment for one of the current user's orders")
    public ResponseEntity<PaymentResponseDto> createPayment(
            @Valid @RequestBody PaymentRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                paymentService.createPayment(
                        currentUserProvider.getCurrentUserId(),
                        requestDto
                )
        );
    }

    @GetMapping
    @Operation(summary = "Get all payments for the current user")
    public ResponseEntity<List<PaymentResponseDto>> getMyPayments() {
        return ResponseEntity.ok(
                paymentService.getPaymentsForUser(currentUserProvider.getCurrentUserId())
        );
    }

    @GetMapping("/{paymentId}")
    @Operation(summary = "Get one of the current user's payments by ID")
    public ResponseEntity<PaymentResponseDto> getMyPaymentById(
            @PathVariable Long paymentId) {
        return ResponseEntity.ok(
                paymentService.getPaymentById(
                        currentUserProvider.getCurrentUserId(),
                        paymentId
                )
        );
    }

    @PatchMapping("/admin/{paymentId}/mark-success")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Manually mark a payment as successful (admin only)")
    public ResponseEntity<PaymentResponseDto> markPaymentSuccess(
            @PathVariable Long paymentId) {
        return ResponseEntity.ok(paymentService.markPaymentSuccess(paymentId));
    }

    @PatchMapping("/admin/{paymentId}/mark-failed")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Manually mark a payment as failed (admin only)")
    public ResponseEntity<PaymentResponseDto> markPaymentFailed(
            @PathVariable Long paymentId,
            @RequestParam String reason) {
        return ResponseEntity.ok(paymentService.markPaymentFailed(paymentId, reason));
    }
}