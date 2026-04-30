package com.market.BuyFromHome.dto.requestDto.addressRequestDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressRequest {

    @NotBlank(message = "Street address is required")
    private String streetAddress;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^(\\+234|0)[789][01]\\d{8}$", message = "Enter a valid Nigerian phone number")
    private String phoneNumber;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "Country is required")
    private String country;

    private String landmark;

    private boolean isDefault;
}
