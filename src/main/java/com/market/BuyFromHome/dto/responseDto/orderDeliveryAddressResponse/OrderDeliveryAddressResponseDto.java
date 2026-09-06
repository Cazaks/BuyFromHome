package com.market.BuyFromHome.dto.responseDto.orderDeliveryAddressResponse;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDeliveryAddressResponseDto {

    private String streetAddress;
    private String phoneNumber;
    private String city;
    private String state;
    private String country;
    private String landmark;
}
