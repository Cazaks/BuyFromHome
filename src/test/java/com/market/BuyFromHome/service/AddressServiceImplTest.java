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

        User user = User.builder()
                .userId(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .phoneNumber("08012345678")
                .build();

        AddressRequestDto requestDto = new AddressRequestDto();

        requestDto.setStreetAddress("12 Allen Avenue");
        requestDto.setPhoneNumber("09012345678");
        requestDto.setCity("Ikeja");
        requestDto.setState("Lagos");
        requestDto.setCountry("Nigeria");
        requestDto.setLandmark("Beside First Bank");
        requestDto.setDefault(true);

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
                addressServiceImpl.createAddress(user.getUserId(), requestDto);

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

}