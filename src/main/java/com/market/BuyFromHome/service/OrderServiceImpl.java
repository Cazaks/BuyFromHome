package com.market.BuyFromHome.service;

import com.market.BuyFromHome.dto.requestDto.orderRequest.OrderRequestDto;
import com.market.BuyFromHome.dto.responseDto.orderDeliveryAddressResponse.OrderDeliveryAddressResponseDto;
import com.market.BuyFromHome.dto.responseDto.orderItemResponse.OrderItemResponseDto;
import com.market.BuyFromHome.dto.responseDto.orderResponse.OrderResponseDto;
import com.market.BuyFromHome.exception.AppException;
import com.market.BuyFromHome.model.*;
import com.market.BuyFromHome.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final AddressRepository addressRepository;
    private final ProductSellingMeasurementRepository productSellingMeasurementRepository;

    @Transactional
    @Override
    public OrderResponseDto createOrder(Long userId, OrderRequestDto requestDto){


        Cart cart = cartRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new AppException(
                        "Cart is empty.",
                        HttpStatus.BAD_REQUEST
                ));

        if (cart.getItems().isEmpty()) {
            throw new AppException(
                    "Cart is empty.",
                    HttpStatus.BAD_REQUEST
            );
        }

        Address address = addressRepository
                .findByAddressIdAndUser_UserId(requestDto.getAddressId(), userId)
                .orElseThrow(() -> new AppException(
                        "Address not found.",
                        HttpStatus.NOT_FOUND
                ));

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems()) {

            ProductSellingMeasurement measurement = cartItem.getSellingMeasurement();

            if (!measurement.isEnabled()) {
                throw new AppException(
                        "'" + measurement.getProductOption().getProduct().getProductName()
                                + "' is no longer available.",
                        HttpStatus.BAD_REQUEST
                );
            }

            if (cartItem.getQuantity() > measurement.getQuantityInStock()) {
                throw new AppException(
                        "Insufficient stock for '"
                                + measurement.getProductOption().getProduct().getProductName()
                                + "'.",
                        HttpStatus.BAD_REQUEST
                );
            }

        measurement.setQuantityInStock(
                measurement.getQuantityInStock() - cartItem.getQuantity()
        );
        productSellingMeasurementRepository.save(measurement);

        BigDecimal subtotal = cartItem.getPriceAtTimeOfAdding()
                .multiply(BigDecimal.valueOf(cartItem.getQuantity()));

        totalAmount = totalAmount.add(subtotal);

        OrderItem orderItem = OrderItem.builder()
                .sellingMeasurement(measurement)
                .quantity(cartItem.getQuantity())
                .unitPrice(cartItem.getPriceAtTimeOfAdding())
                .subtotal(subtotal)
                .build();

        orderItems.add(orderItem);
    }

    DeliveryAddress deliveryAddress = DeliveryAddress.builder()
            .streetAddress(address.getStreetAddress())
            .phoneNumber(address.getPhoneNumber())
            .city(address.getCity())
            .state(address.getState())
            .country(address.getCountry())
            .landmark(address.getLandmark())
            .build();

    Order order = Order.builder()
            .orderNumber(generateOrderNumber())
            .user(cart.getUser())
            .totalAmount(totalAmount)
            .paymentMethod(requestDto.getPaymentMethod())
            .deliveryAddress(deliveryAddress)
            .notes(requestDto.getNotes())
            .build();

        for (
    OrderItem item : orderItems) {
        item.setOrder(order);
        order.getItems().add(item);
    }

    Order savedOrder = orderRepository.save(order);

    List<CartItem> itemsToRemove = new ArrayList<>(cart.getItems());
        cartItemRepository.deleteAll(itemsToRemove);
        cart.getItems().clear();

        return mapToResponse(savedOrder);
    }




    private String generateOrderNumber() {
        String datePart = LocalDateTime.now()
                .toLocalDate()
                .toString()
                .replace("-", "");

        String randomPart = UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();

        return "ORD-" + datePart + "-" + randomPart;
    }

    private OrderResponseDto mapToResponse(Order order) {

        List<OrderItemResponseDto> items = order.getItems()
                .stream()
                .map(this::mapItemToResponse)
                .toList();

        OrderDeliveryAddressResponseDto deliveryAddress = null;
        if (order.getDeliveryAddress() != null) {
            deliveryAddress = OrderDeliveryAddressResponseDto.builder()
                    .streetAddress(order.getDeliveryAddress().getStreetAddress())
                    .phoneNumber(order.getDeliveryAddress().getPhoneNumber())
                    .city(order.getDeliveryAddress().getCity())
                    .state(order.getDeliveryAddress().getState())
                    .country(order.getDeliveryAddress().getCountry())
                    .landmark(order.getDeliveryAddress().getLandmark())
                    .build();
        }

        return OrderResponseDto.builder()
                .orderId(order.getOrderId())
                .orderNumber(order.getOrderNumber())
                .userId(order.getUser().getUserId())
                .items(items)
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .paymentStatus(order.getPaymentStatus())
                .paymentMethod(order.getPaymentMethod())
                .deliveryAddress(deliveryAddress)
                .notes(order.getNotes())
                .deliveredAt(order.getDeliveredAt())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    private OrderItemResponseDto mapItemToResponse(OrderItem orderItem) {

        ProductSellingMeasurement measurement = orderItem.getSellingMeasurement();
        ProductOption option = measurement.getProductOption();
        Product product = option.getProduct();

        return OrderItemResponseDto.builder()
                .orderItemId(orderItem.getOrderItemId())
                .productId(product.getProductId())
                .productName(product.getProductName())
                .productOptionId(option.getProductOptionId())
                .productVariety(option.getProductVariety())
                .productSpecification(option.getProductSpecification())
                .sellingMeasurementId(measurement.getSellingMeasurementId())
                .measurementUnit(measurement.getMeasurementUnit().name())
                .quantity(orderItem.getQuantity())
                .unitPrice(orderItem.getUnitPrice())
                .subtotal(orderItem.getSubtotal())
                .build();
    }
}
