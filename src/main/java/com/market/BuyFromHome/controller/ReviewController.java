package com.market.BuyFromHome.controller;

import com.market.BuyFromHome.dto.requestDto.reviewRequest.ReviewRequestDto;
import com.market.BuyFromHome.dto.responseDto.reviewResponse.ReviewResponseDto;
import com.market.BuyFromHome.security.CurrentUserProvider;
import com.market.BuyFromHome.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@Tag(
        name = "Reviews",
        description = "Endpoints for creating and viewing product reviews"
)
public class ReviewController {

    private final ReviewService reviewService;
    private final CurrentUserProvider currentUserProvider;

    @PostMapping
    @Operation(summary = "Create a review for a delivered order's product")
    public ResponseEntity<ReviewResponseDto> createReview(
            @Valid @RequestBody ReviewRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                reviewService.createReview(
                        currentUserProvider.getCurrentUserId(),
                        requestDto
                )
        );
    }

    // Public - anyone (including guests, if this route isn't behind auth)
    // can read a product's reviews.
    @GetMapping("/product/{productId}")
    @Operation(summary = "Get all reviews for a product")
    public ResponseEntity<List<ReviewResponseDto>> getReviewsForProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getReviewsForProduct(productId));
    }

    @GetMapping("/{reviewId}")
    @Operation(summary = "Get a single review by ID")
    public ResponseEntity<ReviewResponseDto> getReviewById(@PathVariable Long reviewId) {
        return ResponseEntity.ok(reviewService.getReviewById(reviewId));
    }

    @PutMapping("/{reviewId}")
    @Operation(summary = "Update the current user's own review")
    public ResponseEntity<ReviewResponseDto> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewRequestDto requestDto) {
        return ResponseEntity.ok(
                reviewService.updateReview(
                        currentUserProvider.getCurrentUserId(),
                        reviewId,
                        requestDto
                )
        );
    }

    // Admin reply endpoint intentionally omitted for now - follow-up feature
}