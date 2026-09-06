package com.market.BuyFromHome.service;

import com.market.BuyFromHome.dto.requestDto.deliveryAddressRequest.DeliveryAddressRequestDto;
import com.market.BuyFromHome.dto.responseDto.addressResponse.AddressResponseDto;

import java.util.List;

public interface AddressService {
    AddressResponseDto createAddress(Long userId, DeliveryAddressRequestDto requestDto);
    List<AddressResponseDto> getAddressesForUser(Long userId);
    AddressResponseDto getAddressById(Long userId, Long addressId);
    AddressResponseDto updateAddress(Long userId, Long addressId, DeliveryAddressRequestDto requestDto);
    AddressResponseDto setDefaultAddress(Long userId, Long addressId);
    AddressResponseDto disableAddress(Long userId, Long addressId);
    AddressResponseDto enableAddress(Long userId, Long addressId);
}