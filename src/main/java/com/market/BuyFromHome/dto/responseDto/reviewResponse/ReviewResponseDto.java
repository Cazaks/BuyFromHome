package com.market.BuyFromHome.dto.responseDto.reviewResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponseDto {

    private Long reviewId;

    private Long productId;

    private Long userId;

    private String reviewerName;

    private Integer rating;

    private String comment;

    private LocalDateTime createdAt;
}