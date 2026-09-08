package com.market.BuyFromHome.service;


import com.market.BuyFromHome.dto.requestDto.reviewRequest.ReviewRequestDto;
import com.market.BuyFromHome.dto.responseDto.reviewResponse.ReviewResponseDto;
import com.market.BuyFromHome.enums.OrderStatus;
import com.market.BuyFromHome.exception.AppException;
import com.market.BuyFromHome.model.*;
import com.market.BuyFromHome.repository.OrderRepository;
import com.market.BuyFromHome.repository.ProductRepository;
import com.market.BuyFromHome.repository.ReviewRepository;
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
class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ReviewServiceImpl reviewServiceImpl;


    // ==========================
    // CREATE REVIEW TESTS
    // ==========================

    @Test
    @DisplayName("Should create review successfully for a delivered order containing the product")
    void shouldCreateReviewSuccessfully() {

        User user = buildUser();
        Product product = buildProduct(10L);
        Order order = buildDeliveredOrder(1L, product);
        ReviewRequestDto requestDto = buildRequestDto(product.getProductId(), order.getOrderId());

        when(userRepository.findById(user.getUserId()))
                .thenReturn(Optional.of(user));

        when(orderRepository.findByOrderIdAndUser_UserId(order.getOrderId(), user.getUserId()))
                .thenReturn(Optional.of(order));

        when(productRepository.findById(product.getProductId()))
                .thenReturn(Optional.of(product));

        when(reviewRepository.existsByUser_UserIdAndProduct_ProductId(user.getUserId(), product.getProductId()))
                .thenReturn(false);

        when(reviewRepository.save(any(Review.class)))
                .thenAnswer(i -> {
                    Review review = i.getArgument(0);
                    review.setReviewId(1L);
                    return review;
                });

        ReviewResponseDto response =
                reviewServiceImpl.createReview(user.getUserId(), requestDto);

        assertThat(response).isNotNull();
        assertThat(response.getReviewId()).isEqualTo(1L);
        assertThat(response.getProductId()).isEqualTo(product.getProductId());
        assertThat(response.getRating()).isEqualTo(5);

        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    @DisplayName("Should throw exception when order is not delivered yet")
    void shouldThrowExceptionWhenOrderNotDelivered() {

        User user = buildUser();
        Product product = buildProduct(10L);
        Order order = buildOrderWithStatus(1L, product, OrderStatus.PENDING);
        ReviewRequestDto requestDto = buildRequestDto(product.getProductId(), order.getOrderId());

        when(userRepository.findById(user.getUserId()))
                .thenReturn(Optional.of(user));

        when(orderRepository.findByOrderIdAndUser_UserId(order.getOrderId(), user.getUserId()))
                .thenReturn(Optional.of(order));

        AppException exception = assertThrows(
                AppException.class,
                () -> reviewServiceImpl.createReview(user.getUserId(), requestDto)
        );

        assertThat(exception.getMessage()).isEqualTo("You can only review products from delivered orders.");
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);

        verify(reviewRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when product was not part of the order")
    void shouldThrowExceptionWhenProductNotInOrder() {

        User user = buildUser();
        Product purchasedProduct = buildProduct(10L);
        Product differentProduct = buildProduct(99L);
        Order order = buildDeliveredOrder(1L, purchasedProduct);
        ReviewRequestDto requestDto = buildRequestDto(differentProduct.getProductId(), order.getOrderId());

        when(userRepository.findById(user.getUserId()))
                .thenReturn(Optional.of(user));

        when(orderRepository.findByOrderIdAndUser_UserId(order.getOrderId(), user.getUserId()))
                .thenReturn(Optional.of(order));

        when(productRepository.findById(differentProduct.getProductId()))
                .thenReturn(Optional.of(differentProduct));

        AppException exception = assertThrows(
                AppException.class,
                () -> reviewServiceImpl.createReview(user.getUserId(), requestDto)
        );

        assertThat(exception.getMessage()).isEqualTo("This product was not part of the specified order.");
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);

        verify(reviewRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when user already reviewed this product")
    void shouldThrowExceptionWhenAlreadyReviewed() {

        User user = buildUser();
        Product product = buildProduct(10L);
        Order order = buildDeliveredOrder(1L, product);
        ReviewRequestDto requestDto = buildRequestDto(product.getProductId(), order.getOrderId());

        when(userRepository.findById(user.getUserId()))
                .thenReturn(Optional.of(user));

        when(orderRepository.findByOrderIdAndUser_UserId(order.getOrderId(), user.getUserId()))
                .thenReturn(Optional.of(order));

        when(productRepository.findById(product.getProductId()))
                .thenReturn(Optional.of(product));

        when(reviewRepository.existsByUser_UserIdAndProduct_ProductId(user.getUserId(), product.getProductId()))
                .thenReturn(true);

        AppException exception = assertThrows(
                AppException.class,
                () -> reviewServiceImpl.createReview(user.getUserId(), requestDto)
        );

        assertThat(exception.getMessage()).isEqualTo("You have already reviewed this product.");
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.CONFLICT);

        verify(reviewRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when order does not belong to user")
    void shouldThrowExceptionWhenOrderNotOwnedByUser() {

        ReviewRequestDto requestDto = buildRequestDto(10L, 1L);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(buildUser()));

        when(orderRepository.findByOrderIdAndUser_UserId(1L, 1L))
                .thenReturn(Optional.empty());

        AppException exception = assertThrows(
                AppException.class,
                () -> reviewServiceImpl.createReview(1L, requestDto)
        );

        assertThat(exception.getMessage()).isEqualTo("Order not found.");
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }



    // ==========================
    // GET REVIEW TESTS
    // ==========================

    @Test
    @DisplayName("Should get all reviews for a product")
    void shouldGetReviewsForProduct() {

        User user = buildUser();
        Product product = buildProduct(10L);
        Order order = buildDeliveredOrder(1L, product);
        Review review = buildReview(1L, user, product, order);

        when(reviewRepository.findByProduct_ProductId(product.getProductId()))
                .thenReturn(List.of(review));

        List<ReviewResponseDto> responses =
                reviewServiceImpl.getReviewsForProduct(product.getProductId());

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getReviewId()).isEqualTo(1L);
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

    private Product buildProduct(Long productId) {
        return Product.builder()
                .productId(productId)
                .build();
    }

    private OrderItem buildOrderItem(Product product) {
        // OrderItem links to Product indirectly through
        // ProductSellingMeasurement - assuming that entity exposes
        // getProduct(). Adjust here if it's structured differently.
        ProductOption productOption = ProductOption.builder()
                .product(product)
                .build();

        ProductSellingMeasurement sellingMeasurement = ProductSellingMeasurement.builder()
                .productOption(productOption)
                .build();

        return OrderItem.builder()
                .sellingMeasurement(sellingMeasurement)
                .quantity(1)
                .unitPrice(new BigDecimal("5000.00"))
                .subtotal(new BigDecimal("5000.00"))
                .build();
    }

    private Order buildDeliveredOrder(Long orderId, Product product) {
        return buildOrderWithStatus(orderId, product, OrderStatus.DELIVERED);
    }

    private Order buildOrderWithStatus(Long orderId, Product product, OrderStatus status) {
        return Order.builder()
                .orderId(orderId)
                .orderNumber("ORD-" + orderId)
                .status(status)
                .items(List.of(buildOrderItem(product)))
                .build();
    }

    private Review buildReview(Long reviewId, User user, Product product, Order order) {
        return Review.builder()
                .reviewId(reviewId)
                .user(user)
                .product(product)
                .order(order)
                .rating(5)
                .comment("Great product")
                .build();
    }

    private ReviewRequestDto buildRequestDto(Long productId, Long orderId) {
        return ReviewRequestDto.builder()
                .productId(productId)
                .orderId(orderId)
                .rating(5)
                .comment("Great product")
                .build();
    }
}