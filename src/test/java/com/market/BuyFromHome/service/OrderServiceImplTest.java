package com.market.BuyFromHome.service;

import com.market.BuyFromHome.enums.*;
import com.market.BuyFromHome.model.*;
import com.market.BuyFromHome.repository.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


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
    private OrderServiceImpl orderService;

    // ==========================
    // CREATE ORDER TESTS
    // ==========================





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