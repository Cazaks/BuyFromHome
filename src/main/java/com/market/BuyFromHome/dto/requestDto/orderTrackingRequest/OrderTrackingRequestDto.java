package com.market.BuyFromHome.dto.requestDto.orderTrackingRequest;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderTrackingRequestDto {
    @NotBlank(message = "Courier name is required")
    private String courierName;

    @NotBlank(message = "Tracking number is required")
    private String trackingNumber;

    private String trackingUrl;
}