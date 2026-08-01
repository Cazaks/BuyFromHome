package com.market.BuyFromHome.service;

import com.market.BuyFromHome.dto.requestDto.addressRequest.AddressRequestDto;
import com.market.BuyFromHome.dto.responseDto.addressResponse.AddressResponseDto;
import com.market.BuyFromHome.exception.AppException;
import com.market.BuyFromHome.model.Address;
import com.market.BuyFromHome.model.User;
import com.market.BuyFromHome.repository.AddressRepository;
import com.market.BuyFromHome.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceImplTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AddressServiceImpl addressServiceImpl;

    @Test
    @DisplayName("Should create address successfully")
    void shouldCreateAddressSuccessfully() {

        User user = buildUser();
        AddressRequestDto requestDto = buildRequestDto();

        when(userRepository.findById(user.getUserId()))
                .thenReturn(Optional.of(user));

        when(addressRepository.save(any(Address.class)))
                .thenAnswer(invocation -> {
                    Address address = invocation.getArgument(0);
                    address.setAddressId(1L);
                    address.setUser(user);
                    return address;
                });

        AddressResponseDto response =
                addressServiceImpl.createAddress(
                        user.getUserId(),
                        requestDto
                );

        assertThat(response).isNotNull();

        assertThat(response.getId())
                .isEqualTo(1L);

        assertThat(response.getStreetAddress())
                .isEqualTo("12 Allen Avenue");

        assertThat(response.getPhoneNumber())
                .isEqualTo("09012345678");

        assertThat(response.getCity())
                .isEqualTo("Ikeja");

        assertThat(response.getState())
                .isEqualTo("Lagos");

        assertThat(response.getCountry())
                .isEqualTo("Nigeria");

        assertThat(response.getLandmark())
                .isEqualTo("Beside First Bank");

        assertThat(response.isDefault())
                .isTrue();

        verify(addressRepository)
                .save(any(Address.class));
    }

    @Test
    @DisplayName("Should throw exception when user does not exist")
    void shouldThrowExceptionWhenUserDoesNotExist() {

        AddressRequestDto requestDto = buildRequestDto();

        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        AppException exception = assertThrows(
                AppException.class,
                () -> addressServiceImpl.createAddress(1L, requestDto)
        );

        assertThat(exception.getMessage())
                .isEqualTo("User not found.");

        assertThat(exception.getStatus())
                .isEqualTo(HttpStatus.NOT_FOUND);

        verify(userRepository)
                .findById(1L);

        verify(addressRepository, never())
                .save(any(Address.class));
    }

    @Test
    @DisplayName("Should throw exception when address already exists")
    void shouldThrowExceptionWhenAddressAlreadyExists() {

        User user = buildUser();
        AddressRequestDto requestDto = buildRequestDto();

        when(userRepository.findById(user.getUserId()))
                .thenReturn(Optional.of(user));

        when(addressRepository
                .existsByUser_UserIdAndStreetAddressIgnoreCaseAndCityIgnoreCaseAndStateIgnoreCaseAndCountryIgnoreCase(
                        user.getUserId(),
                        requestDto.getStreetAddress(),
                        requestDto.getCity(),
                        requestDto.getState(),
                        requestDto.getCountry()
                ))
                .thenReturn(true);

        AppException exception = assertThrows(
                AppException.class,
                () -> addressServiceImpl.createAddress(
                        user.getUserId(),
                        requestDto
                )
        );

        assertThat(exception.getMessage())
                .isEqualTo("Address already exists.");

        assertThat(exception.getStatus())
                .isEqualTo(HttpStatus.BAD_REQUEST);

        verify(userRepository)
                .findById(user.getUserId());

        verify(addressRepository)
                .existsByUser_UserIdAndStreetAddressIgnoreCaseAndCityIgnoreCaseAndStateIgnoreCaseAndCountryIgnoreCase(
                        user.getUserId(),
                        requestDto.getStreetAddress(),
                        requestDto.getCity(),
                        requestDto.getState(),
                        requestDto.getCountry()
                );

        verify(addressRepository, never())
                .save(any(Address.class));
    }

    @Test
    @DisplayName("Should get address by id successfully")
    void shouldGetAddressByIdSuccessfully() {

        User user = buildUser();
        Address address = buildAddress(user);

        when(addressRepository.findById(1L))
                .thenReturn(Optional.of(address));

        AddressResponseDto response =
                addressServiceImpl.getAddressById(1L);

        assertThat(response).isNotNull();

        assertThat(response.getId())
                .isEqualTo(1L);

        assertThat(response.getStreetAddress())
                .isEqualTo("12 Allen Avenue");

        assertThat(response.getPhoneNumber())
                .isEqualTo("09012345678");

        assertThat(response.getCity())
                .isEqualTo("Ikeja");

        assertThat(response.getState())
                .isEqualTo("Lagos");

        assertThat(response.getCountry())
                .isEqualTo("Nigeria");

        assertThat(response.getLandmark())
                .isEqualTo("Beside First Bank");

        assertThat(response.isDefault())
                .isTrue();

        verify(addressRepository)
                .findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when address does not exist")
    void shouldThrowExceptionWhenAddressDoesNotExist() {

        when(addressRepository.findById(1L))
                .thenReturn(Optional.empty());

        AppException exception = assertThrows(
                AppException.class,
                () -> addressServiceImpl.getAddressById(1L)
        );

        assertThat(exception.getMessage())
                .isEqualTo("Address not found.");

        assertThat(exception.getStatus())
                .isEqualTo(HttpStatus.NOT_FOUND);

        verify(addressRepository)
                .findById(1L);
    }

    @Test
    @DisplayName("Should get all addresses successfully")
    void shouldGetAllAddressesSuccessfully() {

        User user = buildUser();

        Address firstAddress = buildAddress(user);

        Address secondAddress =
                Address.builder()
                        .addressId(2L)
                        .streetAddress("25 Admiralty Way")
                        .phoneNumber("08123456789")
                        .city("Lekki")
                        .state("Lagos")
                        .country("Nigeria")
                        .landmark("Near the mall")
                        .isDefault(false)
                        .user(user)
                        .build();

        when(addressRepository.findByUser_UserId(user.getUserId()))
                .thenReturn(List.of(firstAddress, secondAddress));

        List<AddressResponseDto> response =
                addressServiceImpl.getAllAddresses(user.getUserId());

        assertThat(response)
                .hasSize(2);

        assertThat(response.get(0).getId())
                .isEqualTo(1L);

        assertThat(response.get(0).getStreetAddress())
                .isEqualTo("12 Allen Avenue");

        assertThat(response.get(0).isDefault())
                .isTrue();

        assertThat(response.get(1).getId())
                .isEqualTo(2L);

        assertThat(response.get(1).getStreetAddress())
                .isEqualTo("25 Admiralty Way");

        assertThat(response.get(1).getPhoneNumber())
                .isEqualTo("08123456789");

        assertThat(response.get(1).isDefault())
                .isFalse();

        verify(addressRepository)
                .findByUser_UserId(user.getUserId());
    }

    private User buildUser() {

        return User.builder()
                .userId(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .phoneNumber("08012345678")
                .build();
    }

    private Address buildAddress(User user) {

        return Address.builder()
                .addressId(1L)
                .streetAddress("12 Allen Avenue")
                .phoneNumber("09012345678")
                .city("Ikeja")
                .state("Lagos")
                .country("Nigeria")
                .landmark("Beside First Bank")
                .isDefault(true)
                .user(user)
                .build();
    }

    private AddressRequestDto buildRequestDto() {

        AddressRequestDto dto =
                new AddressRequestDto();

        dto.setStreetAddress("12 Allen Avenue");
        dto.setPhoneNumber("09012345678");
        dto.setCity("Ikeja");
        dto.setState("Lagos");
        dto.setCountry("Nigeria");
        dto.setLandmark("Beside First Bank");
        dto.setDefault(true);

        return dto;
    }

    private AddressRequestDto buildUpdateRequestDto() {

        AddressRequestDto dto =
                new AddressRequestDto();

        dto.setStreetAddress("25 Admiralty Way");
        dto.setPhoneNumber("08123456789");
        dto.setCity("Lekki");
        dto.setState("Lagos");
        dto.setCountry("Nigeria");
        dto.setLandmark("Near the mall");
        dto.setDefault(false);

        return dto;
    }

}