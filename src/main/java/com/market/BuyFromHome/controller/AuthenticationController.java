package com.market.BuyFromHome.controller;

import com.market.BuyFromHome.dto.requestDto.userRequestDto.GoogleAuthRequest;
import com.market.BuyFromHome.dto.requestDto.userRequestDto.UserLoginRequest;
import com.market.BuyFromHome.dto.requestDto.userRequestDto.UserRegisterRequest;
import com.market.BuyFromHome.dto.responseDto.userResposeDto.AuthResponseDto;
import com.market.BuyFromHome.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(
        name = "Authentication",
        description = "Authentication endpoints for local and Google authentication"
)
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    // ==========================
    // LOCAL REGISTRATION
    // ==========================
    @PostMapping("/register")
    @Operation(summary = "Register a new user using email and password")
    public ResponseEntity<AuthResponseDto> localRegister(
            @Valid @RequestBody UserRegisterRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authenticationService.localRegister(request));
    }

    // ==========================
    // GOOGLE REGISTRATION
    // ==========================
    @PostMapping("/register/google")
    @Operation(summary = "Register a new user using Google")
    public ResponseEntity<AuthResponseDto> googleRegister(
            @Valid @RequestBody GoogleAuthRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authenticationService.googleRegister(request));
    }

    // ==========================
    // LOCAL LOGIN
    // ==========================
    @PostMapping("/login")
    @Operation(summary = "Authenticate a user using email and password")
    public ResponseEntity<AuthResponseDto> localLogin(
            @Valid @RequestBody UserLoginRequest request) {

        return ResponseEntity.ok(authenticationService.localLogin(request));
    }

    // ==========================
    // GOOGLE LOGIN
    // ==========================
    @PostMapping("/login/google")
    @Operation(summary = "Authenticate a user using Google")
    public ResponseEntity<AuthResponseDto> googleLogin(
            @Valid @RequestBody GoogleAuthRequest request) {

        return ResponseEntity.ok(authenticationService.googleLogin(request));
    }
}