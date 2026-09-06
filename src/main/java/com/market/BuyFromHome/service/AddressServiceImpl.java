package com.market.BuyFromHome.service;

import com.market.BuyFromHome.dto.requestDto.deliveryAddressRequest.DeliveryAddressRequestDto;
import com.market.BuyFromHome.dto.responseDto.addressResponse.AddressResponseDto;
import com.market.BuyFromHome.exception.AppException;
import com.market.BuyFromHome.model.Address;
import com.market.BuyFromHome.model.User;
import com.market.BuyFromHome.repository.AddressRepository;
import com.market.BuyFromHome.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public AddressResponseDto createAddress(Long userId, DeliveryAddressRequestDto requestDto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(
                        "User not found.",
                        HttpStatus.NOT_FOUND
                ));

        if (requestDto.isDefault()) {
            unsetExistingDefault(userId);
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
                .enabled(true)
                .build();

        Address savedAddress = addressRepository.save(address);

        return mapToResponse(savedAddress);
    }

    @Transactional
    @Override
    public List<AddressResponseDto> getAddressesForUser(Long userId) {
        return addressRepository.findByUser_UserId(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    @Override
    public AddressResponseDto getAddressById(Long userId, Long addressId) {

        Address address = addressRepository.findByAddressIdAndUser_UserId(addressId, userId)
                .orElseThrow(() -> new AppException(
                        "Address not found.",
                        HttpStatus.NOT_FOUND
                ));

        return mapToResponse(address);
    }

    @Transactional
    @Override
    public AddressResponseDto updateAddress(Long userId, Long addressId, DeliveryAddressRequestDto requestDto) {

        Address address = addressRepository.findByAddressIdAndUser_UserId(addressId, userId)
                .orElseThrow(() -> new AppException(
                        "Address not found.",
                        HttpStatus.NOT_FOUND
                ));

        if (requestDto.isDefault() && !address.isDefault()) {
            unsetExistingDefault(userId);
        }

        address.setStreetAddress(requestDto.getStreetAddress());
        address.setPhoneNumber(requestDto.getPhoneNumber());
        address.setCity(requestDto.getCity());
        address.setState(requestDto.getState());
        address.setCountry(requestDto.getCountry());
        address.setLandmark(requestDto.getLandmark());
        address.setDefault(requestDto.isDefault());

        Address updatedAddress = addressRepository.save(address);

        return mapToResponse(updatedAddress);
    }

    @Transactional
    @Override
    public AddressResponseDto setDefaultAddress(Long userId, Long addressId) {

        Address address = addressRepository.findByAddressIdAndUser_UserId(addressId, userId)
                .orElseThrow(() -> new AppException(
                        "Address not found.",
                        HttpStatus.NOT_FOUND
                ));

        unsetExistingDefault(userId);

        address.setDefault(true);

        Address updatedAddress = addressRepository.save(address);

        return mapToResponse(updatedAddress);
    }

    @Transactional
    @Override
    public AddressResponseDto disableAddress(Long userId, Long addressId) {

        Address address = addressRepository.findByAddressIdAndUser_UserId(addressId, userId)
                .orElseThrow(() -> new AppException(
                        "Address not found.",
                        HttpStatus.NOT_FOUND
                ));

        address.setEnabled(false);

        Address updatedAddress = addressRepository.save(address);

        return mapToResponse(updatedAddress);
    }

    @Transactional
    @Override
    public AddressResponseDto enableAddress(Long userId, Long addressId) {

        Address address = addressRepository.findByAddressIdAndUser_UserId(addressId, userId)
                .orElseThrow(() -> new AppException(
                        "Address not found.",
                        HttpStatus.NOT_FOUND
                ));

        address.setEnabled(true);

        Address updatedAddress = addressRepository.save(address);

        return mapToResponse(updatedAddress);
    }

    private void unsetExistingDefault(Long userId) {
        List<Address> addresses = addressRepository.findByUser_UserId(userId);
        for (Address existing : addresses) {
            if (existing.isDefault()) {
                existing.setDefault(false);
                addressRepository.save(existing);
            }
        }
    }

    private AddressResponseDto mapToResponse(Address address) {
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