package com.market.BuyFromHome.dto.responseDto.addressResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class AddressResponseDto {
    private Long id;
    private String streetAddress;
    private String phoneNumber;
    private String city;
    private String state;
    private String country;
    private String landmark;
    private boolean isDefault;
    private boolean enabled;
}
