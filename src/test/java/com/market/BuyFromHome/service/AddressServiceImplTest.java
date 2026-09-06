package com.market.BuyFromHome.service;

import com.market.BuyFromHome.dto.requestDto.deliveryAddressRequest.DeliveryAddressRequestDto;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    // ==========================
    // CREATE ADDRESS TESTS
    // ==========================

    @Test
    @DisplayName("Should create address successfully when not set as default")
    void shouldCreateAddressSuccessfully() {

        User user = buildUser();

        DeliveryAddressRequestDto requestDto = buildRequestDto(false);

        when(userRepository.findById(user.getUserId()))
                .thenReturn(Optional.of(user));

        when(addressRepository.save(any(Address.class)))
                .thenAnswer(i -> {
                    Address address = i.getArgument(0);
                    address.setAddressId(1L);
                    return address;
                });

        AddressResponseDto response =
                addressServiceImpl.createAddress(user.getUserId(), requestDto);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getStreetAddress()).isEqualTo("12 Allen Avenue");
        assertThat(response.isDefault()).isFalse();
        assertThat(response.isEnabled()).isTrue();

        verify(userRepository).findById(user.getUserId());
        verify(addressRepository, never()).findByUser_UserId(anyLong());
        verify(addressRepository).save(any(Address.class));
    }

    @Test
    @DisplayName("Should unset existing default when creating a new default address")
    void shouldUnsetExistingDefaultWhenCreatingNewDefaultAddress() {

        User user = buildUser();

        Address existingDefault = buildAddress(user, 1L, true);

        DeliveryAddressRequestDto requestDto = buildRequestDto(true);

        when(userRepository.findById(user.getUserId()))
                .thenReturn(Optional.of(user));

        when(addressRepository.findByUser_UserId(user.getUserId()))
                .thenReturn(List.of(existingDefault));

        when(addressRepository.save(any(Address.class)))
                .thenAnswer(i -> {
                    Address address = i.getArgument(0);
                    if (address.getAddressId() == null) {
                        address.setAddressId(2L);
                    }
                    return address;
                });

        AddressResponseDto response =
                addressServiceImpl.createAddress(user.getUserId(), requestDto);

        assertThat(response.isDefault()).isTrue();
        assertThat(existingDefault.isDefault()).isFalse();

        verify(addressRepository).findByUser_UserId(user.getUserId());
        verify(addressRepository, times(2)).save(any(Address.class));
    }

    @Test
    @DisplayName("Should throw exception when creating address for non-existent user")
    void shouldThrowExceptionWhenCreatingAddressForNonExistentUser() {

        DeliveryAddressRequestDto requestDto = buildRequestDto(false);

        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        AppException exception = assertThrows(
                AppException.class,
                () -> addressServiceImpl.createAddress(1L, requestDto)
        );

        assertThat(exception.getMessage()).isEqualTo("User not found.");
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);

        verify(addressRepository, never()).save(any());
    }

    // ==========================
    // GET ADDRESSES TESTS
    // ==========================

    @Test
    @DisplayName("Should get all addresses for a user")
    void shouldGetAddressesForUser() {

        User user = buildUser();
        Address address = buildAddress(user, 1L, true);

        when(addressRepository.findByUser_UserId(user.getUserId()))
                .thenReturn(List.of(address));

        List<AddressResponseDto> responses =
                addressServiceImpl.getAddressesForUser(user.getUserId());

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should get address by id successfully")
    void shouldGetAddressByIdSuccessfully() {

        User user = buildUser();
        Address address = buildAddress(user, 1L, true);

        when(addressRepository.findByAddressIdAndUser_UserId(1L, user.getUserId()))
                .thenReturn(Optional.of(address));

        AddressResponseDto response =
                addressServiceImpl.getAddressById(user.getUserId(), 1L);

        assertThat(response.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should throw exception when address does not belong to user")
    void shouldThrowExceptionWhenGettingAddressThatDoesNotBelongToUser() {

        when(addressRepository.findByAddressIdAndUser_UserId(1L, 99L))
                .thenReturn(Optional.empty());

        AppException exception = assertThrows(
                AppException.class,
                () -> addressServiceImpl.getAddressById(99L, 1L)
        );

        assertThat(exception.getMessage()).isEqualTo("Address not found.");
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    // ==========================
    // UPDATE ADDRESS TESTS
    // ==========================

    @Test
    @DisplayName("Should update address fields successfully")
    void shouldUpdateAddressSuccessfully() {

        User user = buildUser();
        Address address = buildAddress(user, 1L, false);

        DeliveryAddressRequestDto requestDto = DeliveryAddressRequestDto.builder()
                .streetAddress("45 Bode Thomas Street")
                .phoneNumber("08099998888")
                .city("Surulere")
                .state("Lagos")
                .country("Nigeria")
                .landmark("Near the market")
                .isDefault(false)
                .build();

        when(addressRepository.findByAddressIdAndUser_UserId(1L, user.getUserId()))
                .thenReturn(Optional.of(address));

        when(addressRepository.save(any(Address.class)))
                .thenAnswer(i -> i.getArgument(0));

        AddressResponseDto response =
                addressServiceImpl.updateAddress(user.getUserId(), 1L, requestDto);

        assertThat(response.getStreetAddress()).isEqualTo("45 Bode Thomas Street");
        assertThat(response.getCity()).isEqualTo("Surulere");

        verify(addressRepository, never()).findByUser_UserId(anyLong());
        verify(addressRepository).save(address);
    }

    @Test
    @DisplayName("Should unset other default when updating an address to become default")
    void shouldUnsetOtherDefaultWhenUpdatingAddressToDefault() {

        User user = buildUser();
        Address currentDefault = buildAddress(user, 1L, true);
        Address addressToUpdate = buildAddress(user, 2L, false);

        DeliveryAddressRequestDto requestDto = DeliveryAddressRequestDto.builder()
                .streetAddress(addressToUpdate.getStreetAddress())
                .phoneNumber(addressToUpdate.getPhoneNumber())
                .city(addressToUpdate.getCity())
                .state(addressToUpdate.getState())
                .country(addressToUpdate.getCountry())
                .landmark(addressToUpdate.getLandmark())
                .isDefault(true)
                .build();

        when(addressRepository.findByAddressIdAndUser_UserId(2L, user.getUserId()))
                .thenReturn(Optional.of(addressToUpdate));

        when(addressRepository.findByUser_UserId(user.getUserId()))
                .thenReturn(List.of(currentDefault));

        when(addressRepository.save(any(Address.class)))
                .thenAnswer(i -> i.getArgument(0));

        AddressResponseDto response =
                addressServiceImpl.updateAddress(user.getUserId(), 2L, requestDto);

        assertThat(response.isDefault()).isTrue();
        assertThat(currentDefault.isDefault()).isFalse();

        verify(addressRepository).findByUser_UserId(user.getUserId());
    }

    @Test
    @DisplayName("Should not call unset when address is already the default and stays default")
    void shouldNotCallUnsetWhenAddressIsAlreadyDefaultAndStaysDefault() {

        User user = buildUser();
        Address address = buildAddress(user, 1L, true);

        DeliveryAddressRequestDto requestDto = DeliveryAddressRequestDto.builder()
                .streetAddress(address.getStreetAddress())
                .phoneNumber(address.getPhoneNumber())
                .city(address.getCity())
                .state(address.getState())
                .country(address.getCountry())
                .landmark(address.getLandmark())
                .isDefault(true)
                .build();

        when(addressRepository.findByAddressIdAndUser_UserId(1L, user.getUserId()))
                .thenReturn(Optional.of(address));

        when(addressRepository.save(any(Address.class)))
                .thenAnswer(i -> i.getArgument(0));

        addressServiceImpl.updateAddress(user.getUserId(), 1L, requestDto);

        verify(addressRepository, never()).findByUser_UserId(anyLong());
    }

    @Test
    @DisplayName("Should throw exception when updating a non-existent address")
    void shouldThrowExceptionWhenUpdatingNonExistentAddress() {

        DeliveryAddressRequestDto requestDto = buildRequestDto(false);

        when(addressRepository.findByAddressIdAndUser_UserId(1L, 1L))
                .thenReturn(Optional.empty());

        AppException exception = assertThrows(
                AppException.class,
                () -> addressServiceImpl.updateAddress(1L, 1L, requestDto)
        );

        assertThat(exception.getMessage()).isEqualTo("Address not found.");
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);

        verify(addressRepository, never()).save(any());
    }

    // ==========================
    // SET DEFAULT TESTS
    // ==========================

    @Test
    @DisplayName("Should set address as default and unset the previous default")
    void shouldSetAddressAsDefaultSuccessfully() {

        User user = buildUser();
        Address currentDefault = buildAddress(user, 1L, true);
        Address newDefault = buildAddress(user, 2L, false);

        when(addressRepository.findByAddressIdAndUser_UserId(2L, user.getUserId()))
                .thenReturn(Optional.of(newDefault));

        when(addressRepository.findByUser_UserId(user.getUserId()))
                .thenReturn(List.of(currentDefault));

        when(addressRepository.save(any(Address.class)))
                .thenAnswer(i -> i.getArgument(0));

        AddressResponseDto response =
                addressServiceImpl.setDefaultAddress(user.getUserId(), 2L);

        assertThat(response.isDefault()).isTrue();
        assertThat(currentDefault.isDefault()).isFalse();

        verify(addressRepository, times(2)).save(any(Address.class));
    }

    @Test
    @DisplayName("Should throw exception when setting default on non-existent address")
    void shouldThrowExceptionWhenSettingDefaultOnNonExistentAddress() {

        when(addressRepository.findByAddressIdAndUser_UserId(1L, 1L))
                .thenReturn(Optional.empty());

        AppException exception = assertThrows(
                AppException.class,
                () -> addressServiceImpl.setDefaultAddress(1L, 1L)
        );

        assertThat(exception.getMessage()).isEqualTo("Address not found.");
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);

        verify(addressRepository, never()).save(any());
    }

    // ==========================
    // DISABLE / ENABLE TESTS
    // ==========================

    @Test
    @DisplayName("Should disable address successfully")
    void shouldDisableAddressSuccessfully() {

        User user = buildUser();
        Address address = buildAddress(user, 1L, false);

        when(addressRepository.findByAddressIdAndUser_UserId(1L, user.getUserId()))
                .thenReturn(Optional.of(address));

        when(addressRepository.save(any(Address.class)))
                .thenAnswer(i -> i.getArgument(0));

        AddressResponseDto response =
                addressServiceImpl.disableAddress(user.getUserId(), 1L);

        assertThat(response.isEnabled()).isFalse();
    }

    @Test
    @DisplayName("Should throw exception when disabling a non-existent address")
    void shouldThrowExceptionWhenDisablingNonExistentAddress() {

        when(addressRepository.findByAddressIdAndUser_UserId(1L, 1L))
                .thenReturn(Optional.empty());

        AppException exception = assertThrows(
                AppException.class,
                () -> addressServiceImpl.disableAddress(1L, 1L)
        );

        assertThat(exception.getMessage()).isEqualTo("Address not found.");
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);

        verify(addressRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should enable address successfully")
    void shouldEnableAddressSuccessfully() {

        User user = buildUser();
        Address address = buildAddress(user, 1L, false);
        address.setEnabled(false);

        when(addressRepository.findByAddressIdAndUser_UserId(1L, user.getUserId()))
                .thenReturn(Optional.of(address));

        when(addressRepository.save(any(Address.class)))
                .thenAnswer(i -> i.getArgument(0));

        AddressResponseDto response =
                addressServiceImpl.enableAddress(user.getUserId(), 1L);

        assertThat(response.isEnabled()).isTrue();
    }

    @Test
    @DisplayName("Should throw exception when enabling a non-existent address")
    void shouldThrowExceptionWhenEnablingNonExistentAddress() {

        when(addressRepository.findByAddressIdAndUser_UserId(1L, 1L))
                .thenReturn(Optional.empty());

        AppException exception = assertThrows(
                AppException.class,
                () -> addressServiceImpl.enableAddress(1L, 1L)
        );

        assertThat(exception.getMessage()).isEqualTo("Address not found.");
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);

        verify(addressRepository, never()).save(any());
    }

    // ==========================
    // TEST HELPERS
    // ==========================

    private User buildUser() {
        return User.builder()
                .userId(1L)
                .firstName("Caleb")
                .lastName("Osowo")
                .email("caleb@test.com")
                .build();
    }

    private Address buildAddress(User user, Long addressId, boolean isDefault) {
        return Address.builder()
                .addressId(addressId)
                .user(user)
                .streetAddress("12 Allen Avenue")
                .phoneNumber("08012345678")
                .city("Ikeja")
                .state("Lagos")
                .country("Nigeria")
                .landmark("Beside First Bank")
                .isDefault(isDefault)
                .enabled(true)
                .build();
    }

    private DeliveryAddressRequestDto buildRequestDto(boolean isDefault) {
        return DeliveryAddressRequestDto.builder()
                .streetAddress("12 Allen Avenue")
                .phoneNumber("08012345678")
                .city("Ikeja")
                .state("Lagos")
                .country("Nigeria")
                .landmark("Beside First Bank")
                .isDefault(isDefault)
                .build();
    }
}