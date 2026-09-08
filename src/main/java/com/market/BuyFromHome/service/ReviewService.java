package com.market.BuyFromHome.service;

import com.market.BuyFromHome.dto.requestDto.reviewRequest.ReviewRequestDto;
import com.market.BuyFromHome.dto.responseDto.reviewResponse.ReviewResponseDto;
import jakarta.transaction.Transactional;

import java.util.List;

public interface ReviewService {
    @Transactional
    ReviewResponseDto createReview(Long userId, ReviewRequestDto requestDto);

    @Transactional
    List<ReviewResponseDto> getReviewsForProduct(Long productId);

    @Transactional
    ReviewResponseDto getReviewById(Long reviewId);

    @Transactional
    ReviewResponseDto updateReview(Long userId, Long reviewId, ReviewRequestDto requestDto);
}
