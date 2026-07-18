package com.market.BuyFromHome.controller;

import com.market.BuyFromHome.dto.requestDto.userRequestDto.GoogleAuthRequest;
import com.market.BuyFromHome.dto.requestDto.userRequestDto.UserLoginRequest;
import com.market.BuyFromHome.dto.requestDto.userRequestDto.UserRegisterRequest;
import com.market.BuyFromHome.dto.responseDto.userResposeDto.AuthResponseDto;
import com.market.BuyFromHome.service.AuthenticationServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationServiceImpl authenticationService;

    // ==========================
    // LOCAL REGISTRATION
    // ==========================
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> localRegister(
            @Valid @RequestBody UserRegisterRequest request) {

        AuthResponseDto response =
                authenticationService.localRegister(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // ==========================
    // GOOGLE REGISTRATION
    // ==========================
    @PostMapping("/register/google")
    public ResponseEntity<AuthResponseDto> googleRegister(
            @Valid @RequestBody GoogleAuthRequest request) {

        AuthResponseDto response =
                authenticationService.googleRegister(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // ==========================
    // LOCAL LOGIN
    // ==========================
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> localLogin(
            @RequestBody UserLoginRequest request) {

        AuthResponseDto response =
                authenticationService.localLogin(request);

        return ResponseEntity.ok(response);
    }

    // ==========================
    // GOOGLE LOGIN
    // ==========================
    @PostMapping("/login/google")
    public ResponseEntity<AuthResponseDto> googleLogin(
            @Valid @RequestBody GoogleAuthRequest request) {

        AuthResponseDto response =
                authenticationService.googleLogin(request);

        return ResponseEntity.ok(response);
    }
}
