package com.market.BuyFromHome.service;

import com.market.BuyFromHome.dto.requestDto.addressRequest.AddressRequestDto;
import com.market.BuyFromHome.dto.responseDto.addressResponse.AddressResponseDto;

import java.util.List;

public interface AddressService {
    AddressResponseDto createAddress(
            Long userId, AddressRequestDto requestDto);

    AddressResponseDto setDefaultAddress(
            Long userId,
            Long addressId);

    AddressResponseDto getAddressById(Long addressId);

    List<AddressResponseDto> getAllAddresses(Long userId);

    AddressResponseDto updateAddress(
            Long userId,
            Long addressId,
            AddressRequestDto requestDto);

    AddressResponseDto disableAddress(
            Long userId,
            Long addressId);
}
