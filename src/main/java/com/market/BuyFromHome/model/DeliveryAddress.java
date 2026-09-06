package com.market.BuyFromHome.model;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryAddress {

    private String streetAddress;

    private String phoneNumber;

    private String city;

    private String state;

    private String country;

    private String landmark;
}