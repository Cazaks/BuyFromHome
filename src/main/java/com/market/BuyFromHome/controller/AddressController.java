package com.market.BuyFromHome.controller;

import com.market.BuyFromHome.dto.requestDto.addressRequest.AddressRequestDto;
import com.market.BuyFromHome.dto.responseDto.addressResponse.AddressResponseDto;
import com.market.BuyFromHome.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    // ==========================
    // CREATE ADDRESS
    // ==========================
    @PostMapping
    public ResponseEntity<AddressResponseDto> createAddress(
            @PathVariable Long userId,
            @Valid @RequestBody AddressRequestDto requestDto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(addressService.createAddress(userId, requestDto));
    }

    // ==========================
    // GET ALL USER ADDRESSES
    // ==========================
    @GetMapping
    public ResponseEntity<List<AddressResponseDto>> getAllAddresses(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                addressService.getAllAddresses(userId));
    }

    // ==========================
    // GET ADDRESS BY ID
    // ==========================
    @GetMapping("/{addressId}")
    public ResponseEntity<AddressResponseDto> getAddressById(
            @PathVariable Long addressId) {

        return ResponseEntity.ok(
                addressService.getAddressById(addressId));
    }

    // ==========================
    // UPDATE ADDRESS
    // ==========================
    @PutMapping("/{addressId}")
    public ResponseEntity<AddressResponseDto> updateAddress(
            @PathVariable Long userId,
            @PathVariable Long addressId,
            @Valid @RequestBody AddressRequestDto requestDto) {

        return ResponseEntity.ok(
                addressService.updateAddress(
                        userId,
                        addressId,
                        requestDto
                ));
    }

    // ==========================
    // SET DEFAULT ADDRESS
    // ==========================
    @PatchMapping("/{addressId}/default")
    public ResponseEntity<AddressResponseDto> setDefaultAddress(
            @PathVariable Long userId,
            @PathVariable Long addressId) {

        return ResponseEntity.ok(
                addressService.setDefaultAddress(
                        userId,
                        addressId
                ));
    }

    // ==========================
    // DISABLE ADDRESS
    // ==========================
    @PatchMapping("/{addressId}/disable")
    public ResponseEntity<AddressResponseDto> disableAddress(
            @PathVariable Long userId,
            @PathVariable Long addressId) {

        return ResponseEntity.ok(
                addressService.disableAddress(
                        userId,
                        addressId
                ));
    }

    // ==========================
    // ENABLE ADDRESS
    // ==========================
    @PatchMapping("/{addressId}/enable")
    public ResponseEntity<AddressResponseDto> enableAddress(
            @PathVariable Long userId,
            @PathVariable Long addressId) {

        return ResponseEntity.ok(
                addressService.enableAddress(
                        userId,
                        addressId
                ));
    }
}
