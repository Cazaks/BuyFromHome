package com.market.BuyFromHome.dto.requestDto.deliveryAddressRequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryAddressRequestDto {

    @NotBlank(message = "Street address is required")
    @Size(
            max = 255,
            message = "Street address cannot exceed 255 characters"
    )
    private String streetAddress;

    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^[0-9+()\\-\\s]{7,20}$",
            message = "Invalid phone number"
    )
    private String phoneNumber;

    @NotBlank(message = "City is required")
    @Size(
            max = 100,
            message = "City cannot exceed 100 characters"
    )
    private String city;

    @NotBlank(message = "State is required")
    @Size(
            max = 100,
            message = "State cannot exceed 100 characters"
    )
    private String state;

    @NotBlank(message = "Country is required")
    @Size(
            max = 100,
            message = "Country cannot exceed 100 characters"
    )
    private String country;

    @Size(
            max = 255,
            message = "Landmark cannot exceed 255 characters"
    )
    private String landmark;

    private boolean isDefault;
}
