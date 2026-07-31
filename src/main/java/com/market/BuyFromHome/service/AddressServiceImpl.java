package com.market.BuyFromHome.service;

import com.market.BuyFromHome.dto.requestDto.addressRequest.AddressRequestDto;
import com.market.BuyFromHome.dto.responseDto.addressResponse.AddressResponseDto;
import com.market.BuyFromHome.exception.AppException;
import com.market.BuyFromHome.model.Address;
import com.market.BuyFromHome.model.User;
import com.market.BuyFromHome.repository.AddressRepository;
import com.market.BuyFromHome.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService{

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Override
    public AddressResponseDto createAddress(Long userId, AddressRequestDto requestDto){

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new AppException(
                                "User not found.",
                                HttpStatus.NOT_FOUND
                        ));

        Address address = Address.builder()
                .streetAddress(requestDto.getStreetAddress())
                .phoneNumber(requestDto.getPhoneNumber())
                .city(requestDto.getCity())
                .state(requestDto.getState())
                .country(requestDto.getCountry())
                .landmark(requestDto.getLandmark())
                .isDefault(requestDto.isDefault())
                .user(user)
                .build();

        Address savedAddress = addressRepository.save(address);
        return mapToResponse(savedAddress);

    }

    private AddressResponseDto mapToResponse(Address address){
        return AddressResponseDto.builder()
                .id(address.getAddressId())
                .streetAddress(address.getStreetAddress())
                .phoneNumber(address.getPhoneNumber())
                .city(address.getCity())
                .state(address.getState())
                .country(address.getCountry())
                .landmark(address.getLandmark())
                .isDefault(address.isDefault())
                .build();
    }
}
