package com.market.BuyFromHome.dto.responseDto.orderResponse;

import com.market.BuyFromHome.dto.responseDto.orderDeliveryAddressResponse.OrderDeliveryAddressResponseDto;
import com.market.BuyFromHome.dto.responseDto.orderItemResponse.OrderItemResponseDto;
import com.market.BuyFromHome.enums.OrderStatus;
import com.market.BuyFromHome.enums.PaymentMethod;
import com.market.BuyFromHome.enums.PaymentStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDto {
    private Long orderId;
    private String orderNumber;
    private Long userId;
    private List<OrderItemResponseDto> items;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private PaymentStatus paymentStatus;
    private PaymentMethod paymentMethod;
    private OrderDeliveryAddressResponseDto deliveryAddress;
    private String notes;
    private LocalDateTime deliveredAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}