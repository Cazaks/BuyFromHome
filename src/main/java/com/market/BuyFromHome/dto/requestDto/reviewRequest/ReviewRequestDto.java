package com.market.BuyFromHome.dto.requestDto.reviewRequest;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewRequestDto {

    @NotNull(message = "Product id is required")
    private Long productId;

    // The delivered order that proves this is a verified purchase.
    // Never trust "which product" alone - the service cross-checks that
    // this order actually contains this product and was DELIVERED.
    @NotNull(message = "Order id is required")
    private Long orderId;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Integer rating;

    @NotBlank(message = "Comment is required")
    @Size(max = 2000, message = "Comment must be at most 2000 characters")
    private String comment;
}