package com.market.BuyFromHome.service;

import com.market.BuyFromHome.dto.requestDto.addressRequest.AddressRequestDto;
import com.market.BuyFromHome.dto.responseDto.addressResponse.AddressResponseDto;

public interface AddressService {
    AddressResponseDto createAddress(
            Long userId, AddressRequestDto requestDto);

    AddressResponseDto getAddressById(Long addressId);
}
