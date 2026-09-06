package com.market.BuyFromHome.service;

import com.market.BuyFromHome.dto.requestDto.orderRequest.OrderRequestDto;
import com.market.BuyFromHome.dto.responseDto.orderResponse.OrderResponseDto;
import com.market.BuyFromHome.enums.*;
import com.market.BuyFromHome.exception.AppException;
import com.market.BuyFromHome.model.*;
import com.market.BuyFromHome.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private ProductSellingMeasurementRepository productSellingMeasurementRepository;

    @InjectMocks
    private OrderServiceImpl orderServiceImpl;

    // ==========================
    // CREATE ORDER TESTS
    // ==========================
    @Test
    @DisplayName("Should create order successfully and clear the cart")
    void shouldCreateOrderSuccessfully() {

        User user = buildUser();
        Cart cart = buildCart(user);
        ProductSellingMeasurement measurement = buildSellingMeasurement();
        measurement.setQuantityInStock(10);

        CartItem cartItem = CartItem.builder()
                .cartItemId(1L)
                .cart(cart)
                .sellingMeasurement(measurement)
                .quantity(2)
                .priceAtTimeOfAdding(measurement.getSellingPrice())
                .build();

        cart.getItems().add(cartItem);

        Address address = buildAddress(user);

        OrderRequestDto requestDto = OrderRequestDto.builder()
                .addressId(address.getAddressId())
                .paymentMethod(PaymentMethod.CASH_ON_DELIVERY)
                .notes("Please call before delivery")
                .build();

        when(cartRepository.findByUser_UserId(user.getUserId()))
                .thenReturn(Optional.of(cart));

        when(addressRepository.findByAddressIdAndUser_UserId(
                address.getAddressId(), user.getUserId()))
                .thenReturn(Optional.of(address));

        when(productSellingMeasurementRepository.save(any(ProductSellingMeasurement.class)))
                .thenAnswer(i -> i.getArgument(0));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(i -> {
                    Order order = i.getArgument(0);
                    order.setOrderId(1L);
                    return order;
                });

        OrderResponseDto response = orderServiceImpl.createOrder(user.getUserId(), requestDto);

        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isEqualTo(user.getUserId());
        assertThat(response.getItems()).hasSize(1);
        assertThat(response.getTotalAmount())
                .isEqualByComparingTo(measurement.getSellingPrice().multiply(BigDecimal.valueOf(2)));
        assertThat(response.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(response.getPaymentStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(response.getPaymentMethod()).isEqualTo(PaymentMethod.CASH_ON_DELIVERY);
        assertThat(response.getDeliveryAddress().getStreetAddress())
                .isEqualTo(address.getStreetAddress());
        assertThat(response.getOrderNumber()).startsWith("ORD-");

        assertThat(measurement.getQuantityInStock()).isEqualTo(8);

        assertThat(cart.getItems()).isEmpty();

        verify(cartItemRepository).deleteAll(anyList());
        verify(productSellingMeasurementRepository).save(measurement);
        verify(orderRepository).save(any(Order.class));
    }


    @Test
    @DisplayName("Should throw exception when cart does not exist")
    void shouldThrowExceptionWhenCartDoesNotExist() {

        User user = buildUser();

        OrderRequestDto requestDto = OrderRequestDto.builder()
                .addressId(1L)
                .paymentMethod(PaymentMethod.CASH_ON_DELIVERY)
                .build();

        when(cartRepository.findByUser_UserId(user.getUserId()))
                .thenReturn(Optional.empty());

        AppException exception = assertThrows(
                AppException.class,
                () -> orderServiceImpl.createOrder(user.getUserId(), requestDto)
        );

        assertThat(exception.getMessage()).isEqualTo("Cart is empty.");
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);

        verifyNoInteractions(addressRepository);
        verifyNoInteractions(orderRepository);
    }

    @Test
    @DisplayName("Should throw exception when cart has no items")
    void shouldThrowExceptionWhenCartHasNoItems() {

        User user = buildUser();
        Cart cart = buildCart(user);

        OrderRequestDto requestDto = OrderRequestDto.builder()
                .addressId(1L)
                .paymentMethod(PaymentMethod.CASH_ON_DELIVERY)
                .build();

        when(cartRepository.findByUser_UserId(user.getUserId()))
                .thenReturn(Optional.of(cart));

        AppException exception = assertThrows(
                AppException.class,
                () -> orderServiceImpl.createOrder(user.getUserId(), requestDto)
        );

        assertThat(exception.getMessage()).isEqualTo("Cart is empty.");
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);

        verifyNoInteractions(addressRepository);
        verifyNoInteractions(orderRepository);
    }


    @Test
    @DisplayName("Should throw exception when address does not belong to user")
    void shouldThrowExceptionWhenAddressNotFound() {

        User user = buildUser();
        Cart cart = buildCart(user);
        ProductSellingMeasurement measurement = buildSellingMeasurement();

        CartItem cartItem = CartItem.builder()
                .cart(cart)
                .sellingMeasurement(measurement)
                .quantity(1)
                .priceAtTimeOfAdding(measurement.getSellingPrice())
                .build();

        cart.getItems().add(cartItem);

        OrderRequestDto requestDto = OrderRequestDto.builder()
                .addressId(99L)
                .paymentMethod(PaymentMethod.CASH_ON_DELIVERY)
                .build();

        when(cartRepository.findByUser_UserId(user.getUserId()))
                .thenReturn(Optional.of(cart));

        when(addressRepository.findByAddressIdAndUser_UserId(99L, user.getUserId()))
                .thenReturn(Optional.empty());

        AppException exception = assertThrows(
                AppException.class,
                () -> orderServiceImpl.createOrder(user.getUserId(), requestDto)
        );

        assertThat(exception.getMessage()).isEqualTo("Address not found.");
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);

        verifyNoInteractions(orderRepository);
        verify(productSellingMeasurementRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when a cart item's measurement is disabled")
    void shouldThrowExceptionWhenMeasurementIsDisabled() {

        User user = buildUser();
        Cart cart = buildCart(user);
        ProductSellingMeasurement measurement = buildSellingMeasurement();
        measurement.setEnabled(false);

        CartItem cartItem = CartItem.builder()
                .cart(cart)
                .sellingMeasurement(measurement)
                .quantity(1)
                .priceAtTimeOfAdding(measurement.getSellingPrice())
                .build();

        cart.getItems().add(cartItem);

        Address address = buildAddress(user);

        OrderRequestDto requestDto = OrderRequestDto.builder()
                .addressId(address.getAddressId())
                .paymentMethod(PaymentMethod.CASH_ON_DELIVERY)
                .build();

        when(cartRepository.findByUser_UserId(user.getUserId()))
                .thenReturn(Optional.of(cart));

        when(addressRepository.findByAddressIdAndUser_UserId(
                address.getAddressId(), user.getUserId()))
                .thenReturn(Optional.of(address));

        AppException exception = assertThrows(
                AppException.class,
                () -> orderServiceImpl.createOrder(user.getUserId(), requestDto)
        );

        assertThat(exception.getMessage()).contains("no longer available");
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);

        verifyNoInteractions(orderRepository);
        verify(productSellingMeasurementRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when cart item quantity exceeds stock at checkout")
    void shouldThrowExceptionWhenQuantityExceedsStockAtCheckout() {

        User user = buildUser();
        Cart cart = buildCart(user);
        ProductSellingMeasurement measurement = buildSellingMeasurement();
        measurement.setQuantityInStock(1);

        CartItem cartItem = CartItem.builder()
                .cart(cart)
                .sellingMeasurement(measurement)
                .quantity(3)
                .priceAtTimeOfAdding(measurement.getSellingPrice())
                .build();

        cart.getItems().add(cartItem);

        Address address = buildAddress(user);

        OrderRequestDto requestDto = OrderRequestDto.builder()
                .addressId(address.getAddressId())
                .paymentMethod(PaymentMethod.CASH_ON_DELIVERY)
                .build();

        when(cartRepository.findByUser_UserId(user.getUserId()))
                .thenReturn(Optional.of(cart));

        when(addressRepository.findByAddressIdAndUser_UserId(
                address.getAddressId(), user.getUserId()))
                .thenReturn(Optional.of(address));

        AppException exception = assertThrows(
                AppException.class,
                () -> orderServiceImpl.createOrder(user.getUserId(), requestDto)
        );

        assertThat(exception.getMessage()).contains("Insufficient stock");
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);

        verifyNoInteractions(orderRepository);
        verify(productSellingMeasurementRepository, never()).save(any());
        assertThat(cart.getItems()).hasSize(1);
    }

    @Test
    @DisplayName("Should decrement stock correctly for each ordered item")
    void shouldDecrementStockCorrectly() {

        User user = buildUser();
        Cart cart = buildCart(user);
        ProductSellingMeasurement measurement = buildSellingMeasurement();
        measurement.setQuantityInStock(20);

        CartItem cartItem = CartItem.builder()
                .cart(cart)
                .sellingMeasurement(measurement)
                .quantity(4)
                .priceAtTimeOfAdding(measurement.getSellingPrice())
                .build();

        cart.getItems().add(cartItem);

        Address address = buildAddress(user);

        OrderRequestDto requestDto = OrderRequestDto.builder()
                .addressId(address.getAddressId())
                .paymentMethod(PaymentMethod.CASH_ON_DELIVERY)
                .build();

        when(cartRepository.findByUser_UserId(user.getUserId()))
                .thenReturn(Optional.of(cart));

        when(addressRepository.findByAddressIdAndUser_UserId(
                address.getAddressId(), user.getUserId()))
                .thenReturn(Optional.of(address));

        when(productSellingMeasurementRepository.save(any(ProductSellingMeasurement.class)))
                .thenAnswer(i -> i.getArgument(0));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(i -> {
                    Order order = i.getArgument(0);
                    order.setOrderId(1L);
                    return order;
                });

        orderServiceImpl.createOrder(user.getUserId(), requestDto);

        assertThat(measurement.getQuantityInStock()).isEqualTo(16);
    }

    @Test
    @DisplayName("Should get own order by id successfully")
    void shouldGetOwnOrderByIdSuccessfully() {

        User user = buildUser();
        Order order = buildOrder(user);

        when(orderRepository.findByOrderIdAndUser_UserId(order.getOrderId(), user.getUserId()))
                .thenReturn(Optional.of(order));

        OrderResponseDto response =
                orderServiceImpl.getOrderById(user.getUserId(), order.getOrderId());

        assertThat(response).isNotNull();
        assertThat(response.getOrderId()).isEqualTo(order.getOrderId());
    }

    @Test
    @DisplayName("Should throw exception when order does not belong to user")
    void shouldThrowExceptionWhenOrderDoesNotBelongToUser() {

        User user = buildUser();

        when(orderRepository.findByOrderIdAndUser_UserId(1L, user.getUserId()))
                .thenReturn(Optional.empty());

        AppException exception = assertThrows(
                AppException.class,
                () -> orderServiceImpl.getOrderById(user.getUserId(), 1L)
        );

        assertThat(exception.getMessage()).isEqualTo("Order not found.");
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Should get all orders for a user")
    void shouldGetOrdersForUser() {

        User user = buildUser();
        Order order = buildOrder(user);

        when(orderRepository.findByUser_UserIdOrderByCreatedAtDesc(user.getUserId()))
                .thenReturn(List.of(order));

        List<OrderResponseDto> responses = orderServiceImpl.getOrdersForUser(user.getUserId());

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getOrderId()).isEqualTo(order.getOrderId());
    }


    // ==========================
    // ADMIN TESTS
    // ==========================

    @Test
    @DisplayName("Should get all orders as admin")
    void shouldGetAllOrdersAsAdmin() {

        User user = buildUser();
        Order order = buildOrder(user);

        when(orderRepository.findAll()).thenReturn(List.of(order));

        List<OrderResponseDto> responses = orderServiceImpl.getAllOrders();

        assertThat(responses).hasSize(1);
    }

    @Test
    @DisplayName("Should get any order by id as admin")
    void shouldGetOrderByIdAsAdmin() {

        User user = buildUser();
        Order order = buildOrder(user);

        when(orderRepository.findById(order.getOrderId()))
                .thenReturn(Optional.of(order));

        OrderResponseDto response = orderServiceImpl.getOrderByIdAdmin(order.getOrderId());

        assertThat(response.getOrderId()).isEqualTo(order.getOrderId());
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

    private Cart buildCart(User user) {
        return Cart.builder()
                .cartId(1L)
                .user(user)
                .status(CartStatus.ACTIVE)
                .items(new ArrayList<>())
                .build();
    }

    private Address buildAddress(User user) {
        return Address.builder()
                .addressId(1L)
                .user(user)
                .streetAddress("12 Allen Avenue")
                .phoneNumber("08012345678")
                .city("Ikeja")
                .state("Lagos")
                .country("Nigeria")
                .landmark("Beside First Bank")
                .isDefault(true)
                .enabled(true)
                .build();
    }

    private ProductSellingMeasurement buildSellingMeasurement() {

        ProductCategory category = ProductCategory.builder()
                .id(1L)
                .name("Grains")
                .build();

        Product product = Product.builder()
                .productId(1L)
                .productName("Rice")
                .category(category)
                .build();

        ProductOption productOption = ProductOption.builder()
                .productOptionId(1L)
                .product(product)
                .productVariety("Local Rice")
                .productSpecification("Short Grain")
                .build();

        return ProductSellingMeasurement.builder()
                .sellingMeasurementId(1L)
                .productOption(productOption)
                .measurementUnit(MeasurementUnit.BAG)
                .sellingPrice(new BigDecimal("8000.00"))
                .quantityInStock(20)
                .enabled(true)
                .build();
    }

    private Order buildOrder(User user) {

        ProductSellingMeasurement measurement = buildSellingMeasurement();

        OrderItem orderItem = OrderItem.builder()
                .orderItemId(1L)
                .sellingMeasurement(measurement)
                .quantity(2)
                .unitPrice(measurement.getSellingPrice())
                .subtotal(measurement.getSellingPrice().multiply(BigDecimal.valueOf(2)))
                .build();

        List<OrderItem> items = new ArrayList<>();
        items.add(orderItem);

        Order order = Order.builder()
                .orderId(1L)
                .orderNumber("ORD-20260906-ABCD1234")
                .user(user)
                .items(items)
                .totalAmount(measurement.getSellingPrice().multiply(BigDecimal.valueOf(2)))
                .status(OrderStatus.PENDING)
                .paymentStatus(PaymentStatus.PENDING)
                .paymentMethod(PaymentMethod.CASH_ON_DELIVERY)
                .deliveryAddress(DeliveryAddress.builder()
                        .streetAddress("12 Allen Avenue")
                        .phoneNumber("08012345678")
                        .city("Ikeja")
                        .state("Lagos")
                        .country("Nigeria")
                        .landmark("Beside First Bank")
                        .build())
                .build();

        orderItem.setOrder(order);

        return order;
    }

}