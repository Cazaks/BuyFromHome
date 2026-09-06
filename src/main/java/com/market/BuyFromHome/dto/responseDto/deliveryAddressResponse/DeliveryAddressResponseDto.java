package com.market.BuyFromHome.dto.responseDto.deliveryAddressResponse;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryAddressResponseDto {
    private Long addressId;

    private String streetAddress;

    private String phoneNumber;

    private String city;

    private String state;

    private String country;

    private String landmark;

    private boolean isDefault;

    private boolean enabled;
}

