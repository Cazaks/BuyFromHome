package com.market.BuyFromHome.controller;

import com.market.BuyFromHome.dto.requestDto.orderRequest.OrderRequestDto;
import com.market.BuyFromHome.dto.requestDto.orderTrackingRequest.OrderTrackingRequestDto;
import com.market.BuyFromHome.dto.responseDto.orderResponse.OrderResponseDto;
import com.market.BuyFromHome.enums.OrderStatus;
import com.market.BuyFromHome.enums.PaymentStatus;
import com.market.BuyFromHome.security.CurrentUserProvider;
import com.market.BuyFromHome.service.OrderService;
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
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(
        name = "Orders",
        description = "Endpoints for managing customer orders"
)
public class OrderController {

    private final OrderService orderService;
    private final CurrentUserProvider currentUserProvider;

    // ==========================
    // CREATE ORDER (from current user's cart)
    // ==========================
    @PostMapping
    @Operation(summary = "Create a new order from the current user's cart")
    public ResponseEntity<OrderResponseDto> createOrder(
            @Valid @RequestBody OrderRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                orderService.createOrder(
                        currentUserProvider.getCurrentUserId(),
                        requestDto
                )
        );
    }

    // ==========================
    // GET CURRENT USER'S ORDERS
    // ==========================
    @GetMapping
    @Operation(summary = "Get all orders for the current user")
    public ResponseEntity<List<OrderResponseDto>> getMyOrders() {
        return ResponseEntity.ok(
                orderService.getOrdersForUser(currentUserProvider.getCurrentUserId())
        );
    }

    // ==========================
    // GET CURRENT USER'S ORDER BY ID
    // ==========================
    @GetMapping("/{orderId}")
    @Operation(summary = "Get one of the current user's orders by ID")
    public ResponseEntity<OrderResponseDto> getMyOrderById(
            @PathVariable Long orderId) {
        return ResponseEntity.ok(
                orderService.getOrderById(
                        currentUserProvider.getCurrentUserId(),
                        orderId
                )
        );
    }

    // ==========================
    // ADMIN: GET ALL ORDERS
    // ==========================
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Retrieve all orders (admin only)")
    public ResponseEntity<List<OrderResponseDto>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    // ==========================
    // ADMIN: GET ANY ORDER BY ID
    // ==========================
    @GetMapping("/admin/{orderId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get any order by ID (admin only)")
    public ResponseEntity<OrderResponseDto> getOrderByIdAdmin(
            @PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderByIdAdmin(orderId));
    }

    // ==========================
    // ADMIN: UPDATE ORDER STATUS
    // ==========================
    @PatchMapping("/admin/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update an order's status (admin only)")
    public ResponseEntity<OrderResponseDto> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam OrderStatus status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, status));
    }

    // ==========================
    // ADMIN: UPDATE PAYMENT STATUS
    // ==========================
    @PatchMapping("/admin/{orderId}/payment-status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update an order's payment status (admin only)")
    public ResponseEntity<OrderResponseDto> updatePaymentStatus(
            @PathVariable Long orderId,
            @RequestParam PaymentStatus paymentStatus) {
        return ResponseEntity.ok(orderService.updatePaymentStatus(orderId, paymentStatus));
    }

    // ==========================
    // TRACKING ORDER
    // ==========================
    @PatchMapping("/admin/{orderId}/tracking")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Add or update tracking info for an order (admin only)")
    public ResponseEntity<OrderResponseDto> updateTracking(
            @PathVariable Long orderId,
            @Valid @RequestBody OrderTrackingRequestDto requestDto) {
        return ResponseEntity.ok(orderService.updateTracking(orderId, requestDto));
    }
}