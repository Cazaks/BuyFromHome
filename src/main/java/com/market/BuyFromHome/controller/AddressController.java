package com.market.BuyFromHome.controller;

import com.market.BuyFromHome.dto.requestDto.deliveryAddressRequest.DeliveryAddressRequestDto;
import com.market.BuyFromHome.dto.responseDto.addressResponse.AddressResponseDto;
import com.market.BuyFromHome.security.CurrentUserProvider;
import com.market.BuyFromHome.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/addresses")
@RequiredArgsConstructor
@Tag(
        name = "Addresses",
        description = "Endpoints for managing a user's delivery addresses"
)
public class AddressController {

    private final AddressService addressService;
    private final CurrentUserProvider currentUserProvider;

    @PostMapping
    @Operation(summary = "Create a new address for the current user")
    public ResponseEntity<AddressResponseDto> createAddress(
            @Valid @RequestBody DeliveryAddressRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                addressService.createAddress(
                        currentUserProvider.getCurrentUserId(),
                        requestDto
                )
        );
    }

    @GetMapping
    @Operation(summary = "Get all addresses for the current user")
    public ResponseEntity<List<AddressResponseDto>> getMyAddresses() {
        return ResponseEntity.ok(
                addressService.getAddressesForUser(currentUserProvider.getCurrentUserId())
        );
    }

    @GetMapping("/{addressId}")
    @Operation(summary = "Get one of the current user's addresses by ID")
    public ResponseEntity<AddressResponseDto> getAddressById(
            @PathVariable Long addressId) {
        return ResponseEntity.ok(
                addressService.getAddressById(
                        currentUserProvider.getCurrentUserId(),
                        addressId
                )
        );
    }

    @PutMapping("/{addressId}")
    @Operation(summary = "Update one of the current user's addresses")
    public ResponseEntity<AddressResponseDto> updateAddress(
            @PathVariable Long addressId,
            @Valid @RequestBody DeliveryAddressRequestDto requestDto) {
        return ResponseEntity.ok(
                addressService.updateAddress(
                        currentUserProvider.getCurrentUserId(),
                        addressId,
                        requestDto
                )
        );
    }

    @PatchMapping("/{addressId}/set-default")
    @Operation(summary = "Set one of the current user's addresses as their default")
    public ResponseEntity<AddressResponseDto> setDefaultAddress(
            @PathVariable Long addressId) {
        return ResponseEntity.ok(
                addressService.setDefaultAddress(
                        currentUserProvider.getCurrentUserId(),
                        addressId
                )
        );
    }

    @PatchMapping("/{addressId}/disable")
    @Operation(summary = "Disable one of the current user's addresses")
    public ResponseEntity<AddressResponseDto> disableAddress(
            @PathVariable Long addressId) {
        return ResponseEntity.ok(
                addressService.disableAddress(
                        currentUserProvider.getCurrentUserId(),
                        addressId
                )
        );
    }

    @PatchMapping("/{addressId}/enable")
    @Operation(summary = "Enable one of the current user's addresses")
    public ResponseEntity<AddressResponseDto> enableAddress(
            @PathVariable Long addressId) {
        return ResponseEntity.ok(
                addressService.enableAddress(
                        currentUserProvider.getCurrentUserId(),
                        addressId
                )
        );
    }
}