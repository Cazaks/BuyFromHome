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

import java.util.List;


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

        boolean addressExists =
                addressRepository
                        .existsByUser_UserIdAndStreetAddressIgnoreCaseAndCityIgnoreCaseAndStateIgnoreCaseAndCountryIgnoreCase(
                                userId,
                                requestDto.getStreetAddress(),
                                requestDto.getCity(),
                                requestDto.getState(),
                                requestDto.getCountry()
                        );

        if (addressExists) {
            throw new AppException(
                    "Address already exists.",
                    HttpStatus.BAD_REQUEST
            );
        }

        List<Address> existingAddresses =
                addressRepository.findByUser_UserId(userId);

        if (existingAddresses.isEmpty()
                && !requestDto.isDefault()) {

            throw new AppException(
                    "First address must be set as default.",
                    HttpStatus.BAD_REQUEST
            );
        }

        if (!existingAddresses.isEmpty()
                && requestDto.isDefault()
                && existingAddresses.stream()
                .anyMatch(Address::isDefault)) {

            throw new AppException(
                    "User already has a default address.",
                    HttpStatus.BAD_REQUEST
            );
        }


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

    @Override
    public AddressResponseDto setDefaultAddress(
            Long userId,
            Long addressId) {

        Address newDefaultAddress =
                addressRepository.findById(addressId)
                        .orElseThrow(() ->
                                new AppException(
                                        "Address not found.",
                                        HttpStatus.NOT_FOUND
                                ));

        if (!newDefaultAddress.getUser().getUserId().equals(userId)) {
            throw new AppException(
                    "Address does not belong to user.",
                    HttpStatus.BAD_REQUEST
            );
        }

        if (!newDefaultAddress.isEnabled()) {
            throw new AppException(
                    "Cannot set a disabled address as default.",
                    HttpStatus.BAD_REQUEST
            );
        }

        List<Address> addresses =
                addressRepository.findByUser_UserId(userId);

        for (Address address : addresses) {
            address.setDefault(
                    address.getAddressId().equals(addressId)
            );
        }

        Address savedAddress =
                addressRepository.save(newDefaultAddress);

        return mapToResponse(savedAddress);
    }

    @Override
    public AddressResponseDto getAddressById(Long addressId) {

        Address address =
                addressRepository.findById(addressId)
                        .orElseThrow(() ->
                                new AppException(
                                        "Address not found.",
                                        HttpStatus.NOT_FOUND
                                ));

        return mapToResponse(address);
    }

    @Override
    public List<AddressResponseDto> getAllAddresses(Long userId) {

        List<Address> addresses =
                addressRepository.findByUser_UserId(userId);

        return addresses.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public AddressResponseDto updateAddress(
            Long userId,
            Long addressId,
            AddressRequestDto requestDto) {

        Address address =
                addressRepository.findById(addressId)
                        .orElseThrow(() ->
                                new AppException(
                                        "Address not found.",
                                        HttpStatus.NOT_FOUND
                                ));

        if (!address.getUser().getUserId().equals(userId)) {
            throw new AppException(
                    "Address does not belong to user.",
                    HttpStatus.BAD_REQUEST
            );
        }

        List<Address> existingAddresses =
                addressRepository.findByUser_UserId(userId);

        if (requestDto.isDefault()
                && existingAddresses.stream()
                .anyMatch(existingAddress ->
                        !existingAddress.getAddressId().equals(addressId)
                                && existingAddress.isDefault())) {

            throw new AppException(
                    "User already has a default address.",
                    HttpStatus.BAD_REQUEST
            );
        }

        if (!requestDto.isDefault()
                && address.isDefault()
                && existingAddresses.stream()
                .noneMatch(existingAddress ->
                        !existingAddress.getAddressId().equals(addressId)
                                && existingAddress.isDefault())) {

            throw new AppException(
                    "User must have a default address.",
                    HttpStatus.BAD_REQUEST
            );
        }

        address.setStreetAddress(requestDto.getStreetAddress());
        address.setPhoneNumber(requestDto.getPhoneNumber());
        address.setCity(requestDto.getCity());
        address.setState(requestDto.getState());
        address.setCountry(requestDto.getCountry());
        address.setLandmark(requestDto.getLandmark());
        address.setDefault(requestDto.isDefault());

        Address updatedAddress =
                addressRepository.save(address);

        return mapToResponse(updatedAddress);
    }

    @Override
    public AddressResponseDto disableAddress(
            Long userId,
            Long addressId) {

        Address address =
                addressRepository.findById(addressId)
                        .orElseThrow(() ->
                                new AppException(
                                        "Address not found.",
                                        HttpStatus.NOT_FOUND
                                ));

        if (!address.getUser().getUserId().equals(userId)) {
            throw new AppException(
                    "Address does not belong to user.",
                    HttpStatus.BAD_REQUEST
            );
        }

        if (address.isDefault()) {
            throw new AppException(
                    "Cannot disable the default address.",
                    HttpStatus.BAD_REQUEST
            );
        }

        address.setEnabled(false);

        Address disabledAddress =
                addressRepository.save(address);

        return mapToResponse(disabledAddress);
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
                .enabled(address.isEnabled())
                .build();
    }
}
