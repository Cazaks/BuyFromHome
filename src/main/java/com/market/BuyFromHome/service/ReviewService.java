package com.market.BuyFromHome.service;

import com.market.BuyFromHome.dto.requestDto.reviewRequest.ReviewRequestDto;
import com.market.BuyFromHome.dto.responseDto.reviewResponse.ReviewResponseDto;
import jakarta.transaction.Transactional;

public interface ReviewService {
    @Transactional
    ReviewResponseDto createReview(Long userId, ReviewRequestDto requestDto);
}
