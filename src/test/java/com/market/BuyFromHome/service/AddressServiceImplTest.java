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

        AddressRequestDto requestDto =
                buildRequestDto();

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
                .thenReturn(false);

        when(addressRepository.save(any(Address.class)))
                .thenAnswer(invocation -> {
                    Address address =
                            invocation.getArgument(0);

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
    @DisplayName("Should throw exception when first address is not set as default")
    void shouldThrowExceptionWhenFirstAddressIsNotDefault() {

        User user = buildUser();

        AddressRequestDto requestDto = buildRequestDto();
        requestDto.setDefault(false);

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
                .thenReturn(false);

        when(addressRepository.findByUser_UserId(user.getUserId()))
                .thenReturn(List.of());

        AppException exception = assertThrows(
                AppException.class,
                () -> addressServiceImpl.createAddress(
                        user.getUserId(),
                        requestDto
                )
        );

        assertThat(exception.getMessage())
                .isEqualTo("First address must be set as default.");

        assertThat(exception.getStatus())
                .isEqualTo(HttpStatus.BAD_REQUEST);

        verify(addressRepository)
                .findByUser_UserId(user.getUserId());

        verify(addressRepository, never())
                .save(any(Address.class));
    }

    @Test
    @DisplayName("Should throw exception when creating another default address")
    void shouldThrowExceptionWhenCreatingAnotherDefaultAddress() {

        User user = buildUser();

        Address existingDefaultAddress =
                buildAddress(user);

        AddressRequestDto requestDto =
                buildRequestDto();

        requestDto.setStreetAddress("13 Yaba Street");
        requestDto.setCity("Lagos");
        requestDto.setDefault(true);

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
                .thenReturn(false);

        when(addressRepository.findByUser_UserId(user.getUserId()))
                .thenReturn(List.of(existingDefaultAddress));

        AppException exception = assertThrows(
                AppException.class,
                () -> addressServiceImpl.createAddress(
                        user.getUserId(),
                        requestDto
                )
        );

        assertThat(exception.getMessage())
                .isEqualTo("User already has a default address.");

        assertThat(exception.getStatus())
                .isEqualTo(HttpStatus.BAD_REQUEST);

        verify(addressRepository)
                .findByUser_UserId(user.getUserId());

        verify(addressRepository, never())
                .save(any(Address.class));
    }

    @Test
    @DisplayName("Should create additional address as non-default")
    void shouldCreateAdditionalAddressAsNonDefault() {

        User user = buildUser();

        Address existingDefaultAddress =
                buildAddress(user);

        AddressRequestDto requestDto =
                buildRequestDto();

        requestDto.setStreetAddress("13 Yaba Street");
        requestDto.setCity("Lagos");
        requestDto.setDefault(false);

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
                .thenReturn(false);

        when(addressRepository.findByUser_UserId(user.getUserId()))
                .thenReturn(List.of(existingDefaultAddress));

        when(addressRepository.save(any(Address.class)))
                .thenAnswer(invocation -> {
                    Address address = invocation.getArgument(0);
                    address.setAddressId(2L);
                    return address;
                });

        AddressResponseDto response =
                addressServiceImpl.createAddress(
                        user.getUserId(),
                        requestDto
                );

        assertThat(response).isNotNull();

        assertThat(response.getId())
                .isEqualTo(2L);

        assertThat(response.getStreetAddress())
                .isEqualTo("13 Yaba Street");

        assertThat(response.isDefault())
                .isFalse();

        assertThat(existingDefaultAddress.isDefault())
                .isTrue();

        verify(addressRepository)
                .findByUser_UserId(user.getUserId());

        verify(addressRepository)
                .save(any(Address.class));
    }

    @Test
    @DisplayName("Should change default address successfully")
    void shouldChangeDefaultAddressSuccessfully() {

        User user = buildUser();

        Address currentDefaultAddress =
                buildAddress(user);

        Address newDefaultAddress =
                Address.builder()
                        .addressId(2L)
                        .streetAddress("13 Yaba Street")
                        .phoneNumber("08123456789")
                        .city("Lagos")
                        .state("Lagos")
                        .country("Nigeria")
                        .landmark("Near the mall")
                        .isDefault(false)
                        .user(user)
                        .build();

        when(addressRepository.findById(2L))
                .thenReturn(Optional.of(newDefaultAddress));

        when(addressRepository.findByUser_UserId(user.getUserId()))
                .thenReturn(List.of(
                        currentDefaultAddress,
                        newDefaultAddress
                ));

        when(addressRepository.save(any(Address.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        AddressResponseDto response =
                addressServiceImpl
                        .setDefaultAddress(
                                user.getUserId(),
                                2L
                        );

        assertThat(currentDefaultAddress.isDefault())
                .isFalse();

        assertThat(newDefaultAddress.isDefault())
                .isTrue();

        assertThat(response).isNotNull();

        assertThat(response.getId())
                .isEqualTo(2L);

        assertThat(response.isDefault())
                .isTrue();

        verify(addressRepository)
                .findById(2L);

        verify(addressRepository)
                .findByUser_UserId(user.getUserId());

        verify(addressRepository, atLeastOnce())
                .save(any(Address.class));
    }

    @Test
    @DisplayName("Should throw exception when setting non-existing address as default")
    void shouldThrowExceptionWhenSettingNonExistingAddressAsDefault() {

        User user = buildUser();

        when(addressRepository.findById(99L))
                .thenReturn(Optional.empty());

        AppException exception = assertThrows(
                AppException.class,
                () -> addressServiceImpl.setDefaultAddress(
                        user.getUserId(),
                        99L
                )
        );

        assertThat(exception.getMessage())
                .isEqualTo("Address not found.");

        assertThat(exception.getStatus())
                .isEqualTo(HttpStatus.NOT_FOUND);

        verify(addressRepository)
                .findById(99L);

        verify(addressRepository, never())
                .findByUser_UserId(anyLong());

        verify(addressRepository, never())
                .save(any(Address.class));
    }

    @Test
    @DisplayName("Should throw exception when setting another user's address as default")
    void shouldThrowExceptionWhenSettingAnotherUsersAddressAsDefault() {

        User user = buildUser();

        User anotherUser =
                User.builder()
                        .userId(2L)
                        .build();

        Address anotherUsersAddress =
                buildAddress(anotherUser);

        when(addressRepository.findById(1L))
                .thenReturn(Optional.of(anotherUsersAddress));

        AppException exception = assertThrows(
                AppException.class,
                () -> addressServiceImpl.setDefaultAddress(
                        user.getUserId(),
                        1L
                )
        );

        assertThat(exception.getMessage())
                .isEqualTo("Address does not belong to user.");

        assertThat(exception.getStatus())
                .isEqualTo(HttpStatus.BAD_REQUEST);

        verify(addressRepository)
                .findById(1L);

        verify(addressRepository, never())
                .findByUser_UserId(anyLong());

        verify(addressRepository, never())
                .save(any(Address.class));
    }

    @Test
    @DisplayName("Should keep address as default when it is already the default")
    void shouldKeepAddressAsDefaultWhenAlreadyDefault() {

        User user = buildUser();

        Address currentDefaultAddress =
                buildAddress(user);

        Address secondAddress =
                Address.builder()
                        .addressId(2L)
                        .streetAddress("13 Yaba Street")
                        .phoneNumber("08123456789")
                        .city("Lagos")
                        .state("Lagos")
                        .country("Nigeria")
                        .landmark("Near the mall")
                        .isDefault(false)
                        .user(user)
                        .build();

        when(addressRepository.findById(1L))
                .thenReturn(Optional.of(currentDefaultAddress));

        when(addressRepository.findByUser_UserId(user.getUserId()))
                .thenReturn(List.of(
                        currentDefaultAddress,
                        secondAddress
                ));

        when(addressRepository.save(any(Address.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        AddressResponseDto response =
                addressServiceImpl
                        .setDefaultAddress(
                                user.getUserId(),
                                1L
                        );

        assertThat(currentDefaultAddress.isDefault())
                .isTrue();

        assertThat(secondAddress.isDefault())
                .isFalse();

        assertThat(response).isNotNull();

        assertThat(response.getId())
                .isEqualTo(1L);

        assertThat(response.isDefault())
                .isTrue();

        verify(addressRepository)
                .findById(1L);

        verify(addressRepository)
                .findByUser_UserId(user.getUserId());

        verify(addressRepository)
                .save(currentDefaultAddress);
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