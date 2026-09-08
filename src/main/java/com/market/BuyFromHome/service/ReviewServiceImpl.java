package com.market.BuyFromHome.service;

import com.market.BuyFromHome.dto.requestDto.reviewRequest.ReviewRequestDto;
import com.market.BuyFromHome.dto.responseDto.reviewResponse.ReviewResponseDto;
import com.market.BuyFromHome.enums.OrderStatus;
import com.market.BuyFromHome.exception.AppException;
import com.market.BuyFromHome.model.Order;
import com.market.BuyFromHome.model.Product;
import com.market.BuyFromHome.model.Review;
import com.market.BuyFromHome.model.User;
import com.market.BuyFromHome.repository.OrderRepository;
import com.market.BuyFromHome.repository.ProductRepository;
import com.market.BuyFromHome.repository.ReviewRepository;
import com.market.BuyFromHome.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Transactional
    @Override
    public ReviewResponseDto createReview(Long userId, ReviewRequestDto requestDto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(
                        "User not found.",
                        HttpStatus.NOT_FOUND
                ));

        // Ownership check baked into the query itself - same IDOR-safe
        // pattern used for Payment: a user can only review against an
        // order that is actually theirs.
        Order order = orderRepository.findByOrderIdAndUser_UserId(requestDto.getOrderId(), userId)
                .orElseThrow(() -> new AppException(
                        "Order not found.",
                        HttpStatus.NOT_FOUND
                ));

        if (order.getStatus() != OrderStatus.DELIVERED) {
            throw new AppException(
                    "You can only review products from delivered orders.",
                    HttpStatus.BAD_REQUEST
            );
        }

        Product product = productRepository.findById(requestDto.getProductId())
                .orElseThrow(() -> new AppException(
                        "Product not found.",
                        HttpStatus.NOT_FOUND
                ));

        // NOTE: OrderItem does not reference Product directly - it goes
        // through ProductSellingMeasurement. Assuming
        // ProductSellingMeasurement.getProduct() exists; if it's named or
        // structured differently, this is the one line to fix.
        boolean purchasedThisProductInThisOrder = order.getItems().stream()
                .anyMatch(item -> item.getSellingMeasurement()
                        .getProductOption()
                        .getProduct()
                        .getProductId()
                        .equals(product.getProductId()));

        if (!purchasedThisProductInThisOrder) {
            throw new AppException(
                    "This product was not part of the specified order.",
                    HttpStatus.BAD_REQUEST
            );
        }

        if (reviewRepository.existsByUser_UserIdAndProduct_ProductId(userId, product.getProductId())) {
            throw new AppException(
                    "You have already reviewed this product.",
                    HttpStatus.CONFLICT
            );
        }

        Review review = Review.builder()
                .user(user)
                .product(product)
                .order(order)
                .rating(requestDto.getRating())
                .comment(requestDto.getComment())
                .build();

        Review savedReview = reviewRepository.save(review);

        return mapToResponse(savedReview);
    }

    @Transactional
    @Override
    public List<ReviewResponseDto> getReviewsForProduct(Long productId) {
        return reviewRepository.findByProduct_ProductId(productId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    @Override
    public ReviewResponseDto getReviewById(Long reviewId) {

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new AppException(
                        "Review not found.",
                        HttpStatus.NOT_FOUND
                ));

        return mapToResponse(review);
    }

    @Transactional
    @Override
    public ReviewResponseDto updateReview(Long userId, Long reviewId, ReviewRequestDto requestDto) {

        Review review = reviewRepository.findByReviewIdAndUser_UserId(reviewId, userId)
                .orElseThrow(() -> new AppException(
                        "Review not found.",
                        HttpStatus.NOT_FOUND
                ));

        // Rating/comment can change - the product, user, and order this
        // review is tied to cannot, since those are what proved the
        // purchase in the first place.
        review.setRating(requestDto.getRating());
        review.setComment(requestDto.getComment());

        Review updatedReview = reviewRepository.save(review);

        return mapToResponse(updatedReview);
    }

    private ReviewResponseDto mapToResponse(Review review) {
        return ReviewResponseDto.builder()
                .reviewId(review.getReviewId())
                .productId(review.getProduct().getProductId())
                .userId(review.getUser().getUserId())
                .reviewerName(review.getUser().getFirstName() + " " + review.getUser().getLastName())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
